package com.marbro.entities.enemies.waddle_dee;

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
import com.marbro.entities.enemies.Enemy;

import static com.marbro.constants.Constantes.*;

public class Waddle_dee extends Actor implements Enemy {
    //Private animaciones
    private Animation_Base_Loop walk;
    private Animation_Base_Loop fall;
    private Animation_Base_Loop hurt;

    //Booleanos de estado
    private boolean jump;

    private World world;
    private Body body;
    private Fixture fixture;

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

    public Waddle_dee(){

    }

    public Waddle_dee(World world, Stage stage, float x, float y, Controlador_Colisiones controlador)
    {
        this.world = world;

        //Cargar las animaciones
        loadAnimations();
        defBody(x,y);

        this.estado = EstadoWaddleDee.CAYENDO;

        life = true;

        this.controlador = controlador;

        createContactListener();

        contador = new ActionTimer();
        pain = new ActionTimer();

    }

    public void defBody(float x, float y) {
        // Define las propiedades del cuerpo
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // Añade al mundo el nuevo cuerpo creado
        body = world.createBody(bodyDef);

        // Atributos físicos del cuerpo
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.25f, 0.25f); // Tamaño del body (hitbox)

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.friction = 5f;
        fixtureDef.restitution = 0.3f;

        //categoryBits: es una etiqueta a una fixture, que será utilizada para manejar colisiones
        fixtureDef.filter.categoryBits = CATEGORY_ENEMY;

        //maskBits: define con qué otras entidades u objetos puede colisiones una fixture
        //fixtureDef.filter.maskBits = CATEGORY_PLAYER | CATEGORY_BLOCK | CATEGORY_WALL;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this); // Asegúrate de que `userData` se asigna correctamente

        shape.dispose();
    }

    private void createContactListener(){
        ColisionesHandlerWaddle colisionesHandler = new ColisionesHandlerWaddle(this);
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
            // Dirección de retroceso basada en la dirección actual del movimiento
            float forceX = lastmove == -1 ? 4f : -4f;
            float forceY = 5f;

            // Aplicar la fuerza al enemigo
            body.applyLinearImpulse(new Vector2(forceX, forceY), body.getWorldCenter(), true);

            // Deshabilitar la colisión entre el enemigo y el jugador por 3 segundos
            disableCollisionForSeconds(body, 0.5f);

            pain.start();
        }

        if (pain.getElapsedTime() > 1f) {
            enableCollisionWithPlayer(body);
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

    public void loadTexture(Array <TextureRegion> animation, String path, int i, int f){
        for (int k = i; i <= f; i++){
            Texture texture = new Texture(Gdx.files.internal(path + i + ".png"));
            animation.add(new TextureRegion(texture));
        }
    }

    public void loadAnimations() {
        Array<TextureRegion> walk_waddle = new Array<>();
        loadTexture(walk_waddle, "entities/waddle_dee/waddle_dee_walk/waddle_dee_walk_", 1, 4);
        this.walk = new Animation_Base_Loop( walk_waddle,0.1f);

        Array<TextureRegion> fall_waddle = new Array<>();
        loadTexture(fall_waddle, "entities/waddle_dee/waddle_dee_fall/waddle_dee_fall_", 1, 2);
        this.fall = new Animation_Base_Loop(fall_waddle,0.1f);

        Array<TextureRegion> hurt_waddle = new Array<>();
        loadTexture(hurt_waddle, "entities/waddle_dee/waddle_dee_hurt/waddle_dee_hurt_", 1, 1);
        this.hurt = new Animation_Base_Loop(hurt_waddle,0.1f);
    }

    public Body getBody(){
        return body;
    }


    public void removeEnemy(){
        detach();
    }
}


