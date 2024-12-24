package com.marbro.entities.enemies.waddle_dee;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.marbro.animation.Animation_Base;
import com.marbro.colisions.Controlador_Colisiones;

import static com.marbro.constants.Constantes.*;

public class Waddle_dee extends Actor {
    private Animation_Base walk;
    private Animation_Base fall;
    private Animation_Base hurt;

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

    //Punto inicial de inicio y fin
    private Vector2 start, end;

    //Colisiones
    private boolean onGround;
    private boolean onSpike;
    private boolean onWall;
    private boolean colPlayer;

    //Controlador
    private Controlador_Colisiones controlador;

    public Waddle_dee(World world, Stage stage, float x, float y, Vector2 start, Vector2 end,
                      Array<TextureRegion> walk,
                      Array<TextureRegion> fall,
                      Array<TextureRegion> hurt,
                      Controlador_Colisiones controlador)
    {
        this.world = world;

        //Cargar las animaciones
        this.walk = new Animation_Base(walk, 0.1f);
        this.fall = new Animation_Base(fall, 0.06f);
        this.hurt = new Animation_Base(hurt, 0.06f);
        defBody(x,y);

        this.estado = EstadoWaddleDee.CAYENDO;

        this.start = start;
        this.end = end;
        life = true;

        this.controlador = controlador;

        createContactListener();
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

        TextureRegion frame = drawWaddle();
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }

    public void detach()
    {
        body.destroyFixture(fixture);
        world.destroyBody(body);
    }

    public TextureRegion drawWaddle() {
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


    // Método para actualizar el estado del jugador
    public void updateEnemyState() {
        if (onGround){
            estado = EstadoWaddleDee.CAMINANDO;
        }

        if (!onGround && !onSpike){
            estado = EstadoWaddleDee.CAYENDO;
        }

        if (estado == EstadoWaddleDee.CAMINANDO){
            if ((body.getPosition().x > start.x && lastmove == -1)){
                body.setLinearVelocity(-2f, 0);
            } else {
                lastmove = 1;
            }

            if ((body.getPosition().x < end.x && lastmove == 1)) {
                body.setLinearVelocity(2f, 0);
            } else {
                lastmove = -1;
            }
        }

        if (onWall){
            lastmove *= -1;
        }

        if (colPlayer){
            estado = EstadoWaddleDee.HURT;
        }
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
}


