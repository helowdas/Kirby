package com.marbro.entities.enemies.Sir_kibble;

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
import com.marbro.entities.enemies.waddle_dee.EstadoWaddleDee;

import static com.marbro.constants.Constantes.*;

public class sir_kibble extends Actor implements Enemy {
    //Private animaciones
    private Animation_Base_Loop walk;
    private Animation_Base_Loop fall;
    private Animation_Base_Loop hurt;

    //Booleanos de estado
    private boolean jump;

    private World world;
    private Body body;
    private Fixture fixture;
    private Fixture fixture2;

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

    public sir_kibble(){

    }

    public sir_kibble(World world, Stage stage, float x, float y, Controlador_Colisiones controlador)
    {
        this.world = world;
        //cargar las animaciones
        loadAnimations();
        defBody(x,y);

        this.estado = EstadoSirKibble.CAYENDO;

        life = true;
        this.controlador = controlador;

        createContactListener();

        contador = new ActionTimer();
        pain = new ActionTimer();

    }

    public void defBody(float x, float y){
        BodyDef def = new BodyDef();
        def.position.set(x,y);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.25f,0.25f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 5f;
        fixtureDef.restitution = 0.3f;

        fixtureDef.filter.categoryBits = CATEGORY_ENEMY;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        fixture2 = body.createFixture(fixtureDef);
        fixture2.setUserData("Sir_kibble");
        shape.dispose();
    }

    private void createContactListener(){
        ColisionesHandlerSirKibble colisionesHandler = new ColisionesHandlerSirKibble(this);
        controlador.addListener(colisionesHandler);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!contador.isRunning()){
            contador.start();
        }
        //Actualizar las animaciones
        walk.update(delta);
        hurt.update(delta);
        fall.update(delta);
        updateEnemyState();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Ajustar la posicion del sprite
        float posX = (body.getPosition().x * 1) - (getWidth() / 2);
        float posY = (body.getPosition().y * 1) - (getHeight() / 2) + 0.25f;

        setPosition(posX, posY);

        // Ajustar el tamaño del actor en píxeles, considerando PPM
        setSize(1f, 1f); // Ajusta estos valores según el tamaño deseado de tu sprite en el mundo Box2D

        TextureRegion frame = drawEnemy();
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }

    public void detach() {
        if (fixture != null) {
            body.destroyFixture(fixture);
            fixture = null;
        }
        if (fixture2 != null) {
            body.destroyFixture(fixture2);
            fixture2 = null;
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
        switch (estado) {
            case CAYENDO:
                frame = fall.getFrame();
                break;
            case HURT:
                frame = hurt.getFrame();
                break;
            default:
                frame = walk.getFrame();
                break;
        }

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
            float forceY = 5f;

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
        //poner logica de ataque

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

    public void loadTexture(Array <TextureRegion> animation, String path, int i, int f){
        for (int k = i; i <= f; i++){
            Texture texture = new Texture(Gdx.files.internal(path + i + ".png"));
            animation.add(new TextureRegion(texture));
        }
    }

    public void loadAnimations() {
        Array<TextureRegion> walk_waddle = new Array<>();
        loadTexture(walk_waddle, "entities/Sir_Kibble/caminar/caminar_kibble_", 0, 4);
        this.walk = new Animation_Base_Loop( walk_waddle,0.1f);

        Array<TextureRegion> fall_waddle = new Array<>();
        loadTexture(fall_waddle, "entities/Sir_Kibble/fall/fall_", 0, 0);
        this.fall = new Animation_Base_Loop(fall_waddle,0.1f);

        Array<TextureRegion> hurt_waddle = new Array<>();
        loadTexture(hurt_waddle, "entities/Sir_Kibble/hurt/hurt_", 0, 0);
        this.hurt = new Animation_Base_Loop(hurt_waddle,0.1f);
    }

    public Body getBody(){
        return body;
    }


    public void removeEnemy(){
        detach();
    }

}






