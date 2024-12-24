package com.marbro.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class Kirby extends Actor {
    //Animaciones
    private Animation_Base stand;
    private Animation_Base walk;
    private Animation_Base fall1;
    private Animation_Base fall2;
    private Animation_Base jumping;

    //Atributos de box2d
    private World world;
    private Body body;
    private Fixture fixture;

    //Estado del jugador
    private EstadoKirby estado;
    private int lastmove = -1;
    private boolean jump;

    //Colisiones del jugador
    public boolean onGround = false;
    public boolean onSpike = false;
    public boolean onWall = false;

    //Controlador de colisiones
    private Controlador_Colisiones controlador;

    //Atributos del jugador
    private boolean life;

    public Kirby(World world, Stage stage, float x, float y,
                 Array<TextureRegion> stand,
                 Array<TextureRegion> walk,
                 Array<TextureRegion> fall1,
                 Array<TextureRegion> fall2,
                 Array<TextureRegion> jumping,
                 Controlador_Colisiones controlador)
    {
        this.world = world;

        //Cargar las animaciones
        this.stand = new Animation_Base(stand, 0.06f);
        this.walk = new Animation_Base(walk, 0.06f);
        this.fall1 = new Animation_Base(fall1, 0.08f);
        this.fall2 = new Animation_Base(fall2, 0.2f);
        this.jumping = new Animation_Base(jumping, 0.5f);
        defBody(x,y);

        this.estado = EstadoKirby.QUIETO;
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

        // Crear y asignar la fixture usando `fixDef`
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.density = 0.0f;
        fixDef.friction = 10f;

        //categoryBits: es una etiqueta a una fixture, que será utilizada para manejar colisiones
        fixDef.filter.categoryBits = CATEGORY_PLAYER;

        //maskBits: define con qué otras entidades u objetos puede colisiones una fixture
        //fixDef.filter.maskBits = CATEGORY_BLOCK | CATEGORY_WALL;

        fixture = body.createFixture(fixDef); // Crear la fixture con `fixDef`
        fixture.setUserData(this); // Asegúrate de que `userData` se asigna correctamente

        shape.dispose();
    }


    private void createContactListener(){
        ColisionesHandlerKirby colisionesHandler = new ColisionesHandlerKirby(this);
        controlador.addListener(colisionesHandler);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //Actualizar las animaciones
        stand.update(delta);
        walk.update(delta);
        fall1.update(delta);
        fall2.update(delta);
        jumping.update(delta);
        updatePlayerState();
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
        TextureRegion frame = drawKirby();
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }

    public void detach()
    {
        body.destroyFixture(fixture);
        world.destroyBody(body);
    }

    public TextureRegion drawKirby() {
        TextureRegion frame;
        switch (estado) {
            case CAMINANDO:
                frame = walk.getFrame();
                break;
            case SALTANDO:
                frame = jumping.getFrame();
                break;
            case CAYENDO1:
                frame = fall1.getFrame();
                break;
            case CAYENDO2:
                frame = fall2.getFrame();
                break;
            default:
                frame = stand.getFrame();
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
    public void updatePlayerState() {
        Vector2 vel = body.getLinearVelocity();

        // Lógica para moverse a la izquierda o derecha
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(5f, vel.y);
            lastmove = 1;
            if (onGround && !jump) {
                estado = EstadoKirby.CAMINANDO;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-5f, vel.y);
            lastmove = -1;
            if (onGround && !jump) {
                estado = EstadoKirby.CAMINANDO;
            }
        } else if (onGround && !jump) {
            // Si está en el suelo y no está saltando, se queda quieto
            body.setLinearVelocity(0f, vel.y);
            estado = EstadoKirby.QUIETO;
        }

        if (vel.x != 0 && onGround) {
            estado = EstadoKirby.CAMINANDO;
        }

        // Lógica para saltar
        if (onGround && Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.setLinearVelocity(vel.x, 5f);
            estado = EstadoKirby.SALTANDO;
            jump = true;
        }

        // Lógica para cambiar estado a cayendo
        if (vel.y < 0 && !onGround) {
            estado = jump ? EstadoKirby.CAYENDO1 : EstadoKirby.CAYENDO2;
        }

        if (vel.y > -3.5f && vel.y < 0f && !onGround && jump) {
            estado = EstadoKirby.CAYENDO1;
        } else if (vel.y < 2f && !onGround) {
            estado = EstadoKirby.CAYENDO2;
        }

        // Ajustar el estado a QUIETO si está en el suelo y no se está moviendo
        if (vel.x == 0 && vel.y == 0 && onGround) {
            estado = EstadoKirby.QUIETO;
            jump = false; // Resetear salto al tocar el suelo
        }

        if (onGround){
            if (vel.x == 0 && vel.y == 0){
                estado = EstadoKirby.QUIETO;
            }
        }

        if (onSpike || body.getPosition().y < -8){
            life = false;
        }

        if (onWall){
            //Lógica cuando choca con una pared
        }
    }

    public boolean isAlive(){
        return life;
    }

    public void setOnGround(boolean colision) {
        onGround = colision;
    }

    public void setOnSpike(boolean colision) {
        onSpike = colision;
    }

    public void setOnWall(boolean colision) {
        onWall = colision;
    }
}
