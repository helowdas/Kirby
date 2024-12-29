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
import com.marbro.animation.Animation_Base_Loop;
import com.marbro.animation.Animation_Base_Normal;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.contador.ActionTimer;
import com.marbro.distance.CalculadoraDistancia;
import com.marbro.entities.enemies.Factory.Enemy;

import static com.marbro.constants.Constantes.*;
//import static com.marbro.constants.ConstantesKirby.*;

public class Kirby extends Actor{
    //Animaciones
    private Animation_Base_Loop stand;
    private Animation_Base_Loop walk;
    private Animation_Base_Loop fall1;
    private Animation_Base_Loop fall2;
    private Animation_Base_Loop jumping;
    private Animation_Base_Normal abs;
    private AnimationHelperKirby animations;

    //Atributos de box2d
    private World world;
    private Body body;
    private Stage stage;
    private Fixture fixture;

    //Estado del jugador
    private EstadoKirby estado;
    private int lastmove = -1;
    private boolean jump;

    //Colisiones del jugador
    public boolean onGround = false;
    public boolean onSpike = false;
    public boolean onWall = false;
    public boolean col = false;

    //Controlador de colisiones
    private Controlador_Colisiones controlador;

    //Atributos del jugador
    private boolean life;

    //Calculadora distancia
    CalculadoraDistancia cal;

    //Contador
    ActionTimer contador;
    private static final float ASPIRACION_DURATION = 2f;
    private static final float ASPIRACION_DELAY = 1.5f;
    private float aspiracionTimer = 0;
    private float aspiracionDelayTimer = 0;

    public Kirby(World world, Stage stage, float x, float y,
                 Array<TextureRegion> stand,
                 Array<TextureRegion> walk,
                 Array<TextureRegion> fall1,
                 Array<TextureRegion> fall2,
                 Array<TextureRegion> jumping,
                 Array<TextureRegion> abs,
                 Controlador_Colisiones controlador)
    {
        this.world = world;
        this.stage = stage;

        //Cargar las animaciones
        this.stand = new Animation_Base_Loop(stand, 0.06f);
        this.walk = new Animation_Base_Loop(walk, 0.06f);
        this.fall1 = new Animation_Base_Loop(fall1, 0.08f);
        this.fall2 = new Animation_Base_Loop(fall2, 0.2f);
        this.jumping = new Animation_Base_Loop(jumping, 0.5f);
        this.abs = new Animation_Base_Normal(abs, 0.08f);
        defBody(x,y);

        this.estado = EstadoKirby.QUIETO;
        life = true;
        this.controlador = controlador;

        createContactListener();

        cal = new CalculadoraDistancia();

        contador = new ActionTimer();

        animations = new AnimationHelperKirby();
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
        if (!contador.isRunning()){
            contador.start();
        }

        animations.update(delta);
        updatePlayerState(delta);
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
        frame = animations.getFrame(estado);

        // Reflejar la imagen de Kirby si está moviéndose a la izquierda
        if (lastmove == -1 && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (lastmove == 1 && frame.isFlipX()) {
            frame.flip(true, false);
        }

        return frame;
    }


    public void updatePlayerState(float delta) {
        Vector2 vel = body.getLinearVelocity();

        manejarMovimiento(vel);
        manejarSalto(vel);
        manejarEstadoEnCaida(vel);
        ajustarEstadoQuieto(vel);
        verificarCondicionesEspeciales();
        manejarAspiracion(delta);
    }

    private void manejarMovimiento(Vector2 vel) {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moverDerecha(vel);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moverIzquierda(vel);
        } else {
            mantenerseQuieto(vel);
        }

        if (vel.x != 0 && onGround && estado != EstadoKirby.ASPIRANDO) {
            estado = EstadoKirby.CAMINANDO;
        }
    }

    private void moverDerecha(Vector2 vel) {
        body.setLinearVelocity(VELOCIDAD, vel.y);
        lastmove = 1;
        if (onGround && !jump && estado != EstadoKirby.ASPIRANDO) {
            estado = EstadoKirby.CAMINANDO;
        }
    }

    private void moverIzquierda(Vector2 vel) {
        body.setLinearVelocity(-VELOCIDAD, vel.y);
        lastmove = -1;
        if (onGround && !jump && estado != EstadoKirby.ASPIRANDO) {
            estado = EstadoKirby.CAMINANDO;
        }
    }

    private void mantenerseQuieto(Vector2 vel) {
        if (estado != EstadoKirby.ASPIRANDO) {
            body.setLinearVelocity(0f, vel.y);
            if(body.getLinearVelocity().y == 0){
                estado = EstadoKirby.QUIETO;
            }
        }
    }

    private void manejarSalto(Vector2 vel) {
        if (!jump && onGround && Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            body.applyLinearImpulse(0, IMPULSE_SALTO, body.getPosition().x, body.getPosition().y, true);
            jump = true;
        }
        if (estado != EstadoKirby.ASPIRANDO && body.getLinearVelocity().y > 0) {
            estado = EstadoKirby.SALTANDO;
        }
    }

    private void manejarEstadoEnCaida(Vector2 vel) {
        if (estado != EstadoKirby.ASPIRANDO) {
            System.out.println(vel.y);
            if (jump && -3.5f < vel.y && vel.y < 0f) {
                    estado = EstadoKirby.CAYENDO1;
            } else {
                if (vel.y < 0 && !onGround) {
                    estado = EstadoKirby.CAYENDO2;
                } else if (-0.5f < vel.y && vel.y < 0f) {
                    estado = EstadoKirby.CAYENDO2;
                } else if (vel.y > -0.5 && vel.y <= 0f && !onGround) {
                    estado = EstadoKirby.CAYENDO2;
                }
            }
        }
    }



    private void ajustarEstadoQuieto(Vector2 vel) {
        if (vel.x == 0 && vel.y == 0 && onGround && estado != EstadoKirby.ASPIRANDO) {
            estado = EstadoKirby.QUIETO;
            jump = false; // Resetear salto al tocar el suelo
        }
    }

    private void verificarCondicionesEspeciales() {
        if (onSpike || body.getPosition().y < -8) {
            life = false;
        }

        if (onWall) {
            // Lógica cuando choca con una pared
        }
    }

    private void manejarAspiracion(float delta) {
        // Gestinar delay entre aspiraciones
        if (aspiracionDelayTimer > 0) {
            aspiracionDelayTimer -= delta;
        }

        // Iniciar aspiración si el delay ha terminado
        if (Gdx.input.isKeyPressed(Input.Keys.F) && aspiracionDelayTimer <= 0) {
            estado = EstadoKirby.ASPIRANDO;
            aspiracionTimer += delta;
            absorber();

            // Detener la aspiración si ha alcanzado la duración máxima
            if (aspiracionTimer >= ASPIRACION_DURATION) {
                estado = EstadoKirby.QUIETO;
                aspiracionTimer = 0;  // Reiniciar el temporizador de la animación
                aspiracionDelayTimer = ASPIRACION_DELAY; // Iniciar timer de delay
            }
        } else if (estado == EstadoKirby.ASPIRANDO) {
            aspiracionDelayTimer = ASPIRACION_DELAY; // Asegurar el delay solo se reinicia si se detuvo la aspiración

            // Mantener la animación mientras se presiona la tecla
            if (!Gdx.input.isKeyPressed(Input.Keys.F)) {
                estado = EstadoKirby.QUIETO;
                aspiracionTimer = 0; // Reiniciar el temporizador y poner en espera
            }
        }
    }

    public boolean isAlive(){
        return life;
    }

    public void absorber() {
        short exc = CATEGORY_BLOCK | CATEGORY_WALL | CATEGORY_SPIKE;
        short inc = CATEGORY_ENEMY;

        Body body = cal.encontrarCuerpoMasCercano(this.body, this.world, 1f, exc, inc);
        if (body != null) {
            Array<Actor> actores = stage.getActors();
            for (Actor actor : actores) {
                if (actor instanceof Enemy) { // Tu clase personalizada
                    Enemy yourActor = (Enemy) actor;
                    if (yourActor.getBody() == body) {
                        yourActor.removeEnemy(); // Eliminar el actor encontrado
                        break;
                    }
                }
            }
        }
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

    public void setCol(boolean col) {
        this.col = col;
    }
}
