package com.marbro.entities.enemies.waddle_dee;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;
import com.marbro.animation.Animation_Base_Loop;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.contador.ActionTimer;
import com.marbro.entities.enemies.Factory.Enemy;
import com.marbro.entities.player.Kirby;

import static com.marbro.constants.Constantes.*;

public class Waddle_dee extends Actor implements Enemy {
    //Private animaciones
    private Animation_Base_Loop walk;
    private Animation_Base_Loop fall;
    private Animation_Base_Loop hurt;
    private AnimationHelperWaddle animations;

    //Booleanos de estado
    private boolean jump;

    private World world;
    private Body body;
    private Fixture fixture;
    private Fixture fixture2;
    private float width;
    private float height;

    //Estado del jugador
    private EstadoWaddleDee estado;
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

    // Atributos para manejar el tiempo
     private long lastCollisionTime = 0;
     private static final long COLLISION_COOLDOWN = 1500; // 1.5 segundos en milisegundos

    //constructor
    public Waddle_dee(World world, Body body,
                      Controlador_Colisiones controlador,
                      float width, float height, Kirby kirby)
    {
        this.world = world;
        this.body = body;
        body.getFixtureList().get(0).setUserData(this);
        this.width = width;
        this.height = height;

        animations = new AnimationHelperWaddle();

        this.estado = EstadoWaddleDee.CAYENDO;

        life = true;

        this.controlador = controlador;
        createContactListener(kirby);

        contador = new ActionTimer();
        pain = new ActionTimer();
        colPlayer = false;
    }

    private void createContactListener(Kirby kirby){
        ColisionesHandlerWaddle colisionesHandler = new ColisionesHandlerWaddle(this, kirby);
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
            estado = EstadoWaddleDee.CAMINANDO;
        }

        if (!onGround && !pain.isRunning() && !onSpike) {
            estado = EstadoWaddleDee.CAYENDO;
        } else if (pain.isRunning()){
            estado = EstadoWaddleDee.HURT;
        }

        if (estado == EstadoWaddleDee.CAMINANDO) {
            if (contador.getElapsedTime() > 1f) {
                lastmove *= -1;
                resetTimer(contador);
            }
            body.setLinearVelocity(2f * lastmove, body.getLinearVelocity().y);
        }

        if (colPlayer) {

            // Verificar si ha pasado suficiente tiempo desde la última colisión
            if (getTimeCollision()) {
                // Actualizar el tiempo de la última colisión
                lastCollisionTime = getCurrentTime();

                // Dirección de retroceso basada en la dirección actual del movimiento
                float forceX = lastmove == -1 ? 4.5f : -4.5f;
                float forceY = 3f;

                // Aplicar la fuerza al enemigo
                body.applyLinearImpulse(new Vector2(forceX, forceY), body.getWorldCenter(), true);
                System.out.println("fuerza");

                // Aquí puedes deshabilitar la colisión por un tiempo si es necesario
                // disableCollisionForSeconds(body, 1.5f);

                pain.start();
                // setColPlayer(false);
            }
        }

        if (pain.getElapsedTime() > 1f) {
            //enableCollisionWithPlayer(body);
            pain.reset();
            pain.pause();
        }
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
        detach();
    }

    public boolean getTimeCollision()
    {
        return getCurrentTime() - lastCollisionTime > COLLISION_COOLDOWN;
    }

    public long getCurrentTime()
    {
        long currentTime = System.currentTimeMillis();
       return currentTime;
    }
}



