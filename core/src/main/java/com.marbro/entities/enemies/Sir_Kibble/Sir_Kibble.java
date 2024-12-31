package com.marbro.entities.enemies.Sir_Kibble;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.scenes.scene2d.Stage;


import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.marbro.animation.Animation_Base_Loop;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.contador.ActionTimer;
import com.marbro.entities.enemies.Factory.Enemy;



import com.marbro.entities.player.Kirby;



import static com.marbro.constants.Constantes.*;

public class Sir_Kibble extends Actor implements Enemy {

    //Private animaciones
    private Animation_Base_Loop walk;
    private Animation_Base_Loop fall;
    private Animation_Base_Loop hurt;
    private AnimationHelperSirKibble animations;

    //Booleanos de estado
    private boolean jump;

    private World world;
    private Body body;
    private Fixture fixture;
    private Fixture fixture2;
    private float width;
    private float height;

    //Estado del jugador
    private EstadoSirKibble estado;
    private int lastmove = -1;

    //Atributos del jugador
    private boolean life;

    //Colisiones
    private boolean onGround;
    private boolean onSpike;
    private boolean onWall;
    private boolean colPlayer;

    //Controlador
    private Controlador_Colisiones controlador;

    //Contador
    private ActionTimer contador;
    private ActionTimer pain;


    public Sir_Kibble(World world, Body body,Controlador_Colisiones controlador, float width, float height, Kirby kirby)
    {
        this.world = world;
        this.body = body;
        body.getFixtureList().get(0).setUserData(this);
        this.width = width;
        this.height = height;

        animations = new AnimationHelperSirKibble();

        this.estado = EstadoSirKibble.CAYENDO;

        life = true;

        this.controlador = controlador;
        createContactListener(kirby);

        contador = new ActionTimer();
        pain = new ActionTimer();
    }

    private void createContactListener(Kirby kirby){

        ColisionesSirKibble colisionesHandler = new ColisionesSirKibble(this);
        controlador.addListener(colisionesHandler);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!contador.isRunning()){
            contador.start();
        }
        //Actualizar las animaciones
        animations.update(delta);
        updateEnemyState();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Ajustar la posicion del sprite
        float posX = (body.getPosition().x * 1) - (getWidth() / 2);
        float posY = (body.getPosition().y * 1) - (getHeight() / 2);

        setPosition(posX, posY);

        // Ajustar el tamaño del actor en píxeles, considerando PPM
        setSize(width/PPM, height/PPM); // Ajusta estos valores según el tamaño deseado de tu sprite en el mundo Box2D

        TextureRegion frame = drawEnemy();
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }

    public void detach()
    {
        for (Fixture aux:body.getFixtureList() )
        {
            body.destroyFixture(aux);
        }

        if (body != null) {
            world.destroyBody(body);
            body = null;
        }

        this.remove(); // Asegúrate de eliminar la entidad del mundo del juego, si es necesario
    }

    @Override
    public TextureRegion drawEnemy() {
        TextureRegion frame;
        frame = animations.getFrame(estado);

        // Reflejar la imagen de Kirby si está moviéndose a la izquierda
        if (lastmove == -1 && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (lastmove == 1 && frame.isFlipX()) {
            frame.flip(true, false);
        }
        return frame;
    }

    @Override
    public void updateEnemyState() {
        if (onGround || onSpike) {
            estado = EstadoSirKibble.CAMINANDO;
        }

        if (!onGround && !pain.isRunning() && !onSpike) {
            estado = EstadoSirKibble.CAYENDO;
        } else if (pain.isRunning()){
            estado = EstadoSirKibble.HURT;
        }

        if (estado == EstadoSirKibble.CAMINANDO) {
            if (contador.getElapsedTime() > 1f) {
                lastmove *= -1;
                resetTimer(contador);
            }
            body.setLinearVelocity(2f * lastmove, body.getLinearVelocity().y);
        }

        if (colPlayer) {
            // Dirección de retroceso basada en la dirección actual del movimiento
            float forceX = lastmove == -1 ? 4f : -4f;
            float forceY = 1f;

            // Aplicar la fuerza al enemigo
            body.applyLinearImpulse(new Vector2(forceX, forceY), body.getWorldCenter(), true);

            // Deshabilitar la colisión entre el enemigo y el jugador por 3 segundos
            disableCollisionForSeconds(body, 1.5f);

            pain.start();
        }

        if (pain.getElapsedTime() > 1f) {
            enableCollisionWithPlayer(body);
            pain.reset();
            pain.pause();
        }
    }

    public void attack(){
        //Ataque del Sir_Kibble
    }

    public void resetTimer(ActionTimer timer){
        timer.reset();
    }

    public boolean isAlive(){
        return life;
    }

    public void setOnGround(boolean colision){
        onGround = colision;
    }

    public void setOnSpike(boolean colision){
        onSpike = colision;
    }

    public void setOnWall(boolean colision){
        onWall = colision;
    }

    public void setColPlayer(boolean colision){
        colPlayer = colision;
    }

    public void disableCollisionForSeconds(Body body, float seconds) {
        for (Fixture fixture : body.getFixtureList()) {
            Filter filter = fixture.getFilterData();
            filter.maskBits &= ~CATEGORY_PLAYER; // Excluir la categoría del jugador
            fixture.setFilterData(filter);
        }

        // Establecer un temporizador para reactivar la colisión después de 'seconds' segundos
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                enableCollisionWithPlayer(body);
            }
        }, seconds);
    }

    public void enableCollisionWithPlayer(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            Filter filter = fixture.getFilterData();
            filter.maskBits |= CATEGORY_PLAYER; // Incluir de nuevo la categoría del jugador
            fixture.setFilterData(filter);
        }
    }


    public Body getBody(){
        return body;
    }

    public void removeEnemy(){
        return;
    }


}
