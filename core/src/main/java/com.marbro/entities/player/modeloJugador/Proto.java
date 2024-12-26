package com.marbro.entities.player.modeloJugador;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class Proto extends Actor {
    //Animaciones
    private ProtoAnimation stand;
    private ProtoAnimation walk;
    private ProtoAnimation fall1;
    private ProtoAnimation fall2;
    private ProtoAnimation jumping;

    //Booleanos de estado
    private boolean jump;

    private World world;
    private Body body;
    private Fixture fixture;

    //Estado del jugador
    private EstadoJugador estado;
    private int lastmove = -1;
    private MyContactListener colisiones;

    public Proto(World world, Stage stage, MyContactListener colisiones, float x, float y,
                 Array<TextureRegion> stand,
                 Array<TextureRegion> walk,
                 Array<TextureRegion> fall1,
                 Array<TextureRegion> fall2,
                 Array<TextureRegion> jumping)
    {
        this.world = world;

        //Cargar las animaciones
        this.stand = new ProtoAnimation(stand, 0.06f);
        this.walk = new ProtoAnimation(walk, 0.06f);
        this.fall1 = new ProtoAnimation(fall1, 0.08f);
        this.fall2 = new ProtoAnimation(fall2, 0.2f);
        this.jumping = new ProtoAnimation(jumping, 0.5f);
        defBody(x,y);

        this.estado = EstadoJugador.QUIETO;
        this.colisiones = colisiones;
    }

    public void defBody(float x, float y) {
        //Define las propiedades del cuerpo
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        //Se añade al mundo el nuevo cuerpo creado para poder interactuar con él
        body = world.createBody(bodyDef);

        //Se le dan atributos físicos al cuerpo
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.25f, 0.25f); //Tamaño del body (hitbox)
        fixture = body.createFixture(shape, 0.0f);
        fixture.setUserData("proto");
        fixture.setFriction(4f);
        shape.dispose();

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

        System.out.println(estado);
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
        System.out.println("Velocidad en y del kirby: " + body.getLinearVelocity().y);
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

    public void updatePlayerState() {
        Vector2 vel = body.getLinearVelocity();

        // Lógica para moverse a la izquierda o derecha
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(5f, vel.y);
            lastmove = 1;
            if (colisiones.isGrounded() && !jump) {
                estado = EstadoJugador.CAMINANDO;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-5f, vel.y);
            lastmove = -1;
            if (colisiones.isGrounded() && !jump) {
                estado = EstadoJugador.CAMINANDO;
            }
        } else if (colisiones.isGrounded() && !jump) {
            // Si está en el suelo y no está saltando, se queda quieto
            body.setLinearVelocity(0f, vel.y);
            estado = EstadoJugador.QUIETO;
        }

        if (vel.x != 0 && colisiones.isGrounded()) {
            estado = EstadoJugador.CAMINANDO;
        }

        // Lógica para saltar
        if (colisiones.isGrounded() && Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.setLinearVelocity(vel.x, 5f);
            estado = EstadoJugador.SALTANDO;
            jump = true;
        }

        // Lógica para cambiar estado a cayendo
        if (vel.y < 0 && !colisiones.isGrounded()) {
            estado = jump ? EstadoJugador.CAYENDO1 : EstadoJugador.CAYENDO2;
        }

        if (vel.y > -3.5f && vel.y < 0f && !colisiones.isGrounded() && jump) {
            estado = EstadoJugador.CAYENDO1;
        } else if (vel.y < 2f && !colisiones.isGrounded()) {
            estado = EstadoJugador.CAYENDO2;
        }

        // Ajustar el estado a QUIETO si está en el suelo y no se está moviendo
        if (vel.x == 0 && vel.y == 0 && colisiones.isGrounded()) {
            estado = EstadoJugador.QUIETO;
            jump = false; // Resetear salto al tocar el suelo
        }

        if (colisiones.isGrounded()){
            if (vel.x == 0 && vel.y == 0){
                estado = EstadoJugador.QUIETO;
            }
        }
    }



}
