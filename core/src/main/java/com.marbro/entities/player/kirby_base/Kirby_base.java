package com.marbro.entities.player.kirby_base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SortedIntList;
import com.marbro.TileMapHelpers.BodyHelperService;
import com.marbro.animation.Animation_Base_Loop;
import com.marbro.animation.Animation_Base_Normal;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.contador.ActionTimer;
import com.marbro.distance.CalculadoraDistancia;
import com.marbro.entities.enemies.Factory.Enemy;
import com.marbro.entities.enemies.Factory.Entity;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee;
import com.marbro.entities.player.FactoryKirby;
import com.marbro.entities.player.Kirby;
import com.marbro.scenes.Hud;
import com.marbro.screens.level1.Level1;
import com.marbro.sounds.SoundHelperKirby;

import java.util.ArrayList;

import static com.marbro.constants.Constantes.*;

public class Kirby_base extends Actor implements Kirby {
    //Animaciones
    private AnimationHelperKirby animations;

    //Atributos de box2d
    private World world;
    private Body body;
    private Stage stage;
    private Fixture fixture;
    private Fixture fixture2;
    private float width;
    private float height;

    //Estado del jugador
    private EstadoKirby estado;
    private int lastmove = -1;
    private boolean jump;

    //Colisiones del jugador
    public boolean onGround = true;
    public boolean onSpike = false;
    public boolean onWallRight = false;
    public boolean onWallLeft = false;
    public boolean onPlatform = false;
    public boolean fly = false;
    public boolean col = false;

    //Controlador de colisiones
    private Controlador_Colisiones controlador;

    //Atributos del jugador
    private boolean life;
    private int salud;

    //Calculadora distancia
    CalculadoraDistancia cal;

    //Contador
    ActionTimer contador;
    private static final float ASPIRACION_DURATION = 2f;
    private static final float ASPIRACION_DELAY = 1.5f;
    private float aspiracionTimer = 0;
    private float aspiracionDelayTimer = 0;

    //Fábrica
    private FactoryKirby factory;

    //Instancia
    private Kirby instancia;

    //Screen
    private Level1 screen;

    //Efectos de sonidos
    private SoundHelperKirby sounds;

    //constructor
    public Kirby_base(World world, Stage stage, Body body,
                      Controlador_Colisiones controlador,
                      float width, float height, Level1 screen, int salud)
    {
        this.world = world;
        this.stage = stage;
        this.body = body;
        body.getFixtureList().get(0).setUserData(this);
        this.width = width;
        this.height = height;

        this.estado = EstadoKirby.QUIETO;
        life = true;

        cal = new CalculadoraDistancia();

        contador = new ActionTimer();

        animations = new AnimationHelperKirby();

        this.controlador = controlador;
        createContactListener();

        this.instancia = instancia;

        this.screen = screen;

        factory = new FactoryKirby();

        sounds = new SoundHelperKirby();

        this.salud = salud;
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
        Hud.setPlayerHealt(salud);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Ajustar la posicion del sprite
        float posX = (body.getPosition().x ) - (getWidth() / 2);
        float posY = (body.getPosition().y ) - (getHeight() / 2);

        setPosition(posX, posY);

        // Ajustar el tamaño del actor en píxeles, considerando PPM
        setSize(width/PPM, height/PPM); // Ajusta estos valores según el tamaño deseado de tu sprite en el mundo Box2D
        TextureRegion frame = drawKirby();
        batch.draw(frame, getX() - 0.3f, getY() - 0.3f, getWidth(), getHeight());
    }

    @Override
    public void detach()
    {
        for (Fixture aux:body.getFixtureList() )
        {
            body.destroyFixture(aux);
        }
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
        verificarCondicionesEspeciales(vel);
        manejarAspiracion(delta);
    }

    private void manejarMovimiento(Vector2 vel)
    {
        if (Gdx.input.isKeyPressed(Input.Keys.D) && !onWallRight)
        {
            moverDerecha(vel);
            onWallLeft = false;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A) && !onWallLeft) {
            moverIzquierda(vel);
            onWallRight = false;
        }
        else {
            mantenerseQuieto(vel);
        }

        if (vel.x != 0 && onGround && estado != EstadoKirby.ASPIRANDO) {
            estado = EstadoKirby.CAMINANDO;
        }
    }

    private void moverDerecha(Vector2 vel) {
        body.setLinearVelocity(VELOCIDAD, vel.y);
        lastmove = 1;
        if (onGround && onPlatform && !jump && estado != EstadoKirby.ASPIRANDO) {
            estado = EstadoKirby.CAMINANDO;
        }
    }

    private void moverIzquierda(Vector2 vel) {
        body.setLinearVelocity(-VELOCIDAD, vel.y);
        lastmove = -1;
        if (onGround && onPlatform && !jump && estado != EstadoKirby.ASPIRANDO) {
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

    private void manejarSalto(Vector2 vel)
    {
        if (!jump && onGround && Gdx.input.isKeyJustPressed(Input.Keys.W))
        {
            body.applyLinearImpulse(0, IMPULSE_SALTO, body.getPosition().x, body.getPosition().y, true);
            jump = true;
            sounds.playSound("jump");
        }

        if (estado != EstadoKirby.ASPIRANDO && body.getLinearVelocity().y > 0 && !onPlatform && !fly) {
            estado = EstadoKirby.SALTANDO;
        }

    }

    private void manejarEstadoEnCaida(Vector2 vel) {
        if (estado != EstadoKirby.ASPIRANDO) {
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

    private void ajustarEstadoQuieto(Vector2 vel)
    {
        if (vel.x == 0 && vel.y == 0 && onGround && estado != EstadoKirby.ASPIRANDO) {
            estado = EstadoKirby.QUIETO;
            jump = false; // Resetear salto al tocar el suelo
        }
        else if (onPlatform && vel.x == 0 && estado != EstadoKirby.ASPIRANDO)
        {
            estado = EstadoKirby.QUIETO;
            jump = false;
        }
    }

    private void verificarCondicionesEspeciales(Vector2 vel) {
        if (onSpike || body.getPosition().y < -8) {
            life = false;
        }

        if((jump && Gdx.input.isKeyPressed(Input.Keys.W)) || (Gdx.input.isKeyPressed(Input.Keys.W) && vel.y < 0))
        {
            if(vel.y <= 1.8f)
            {
                jump = true;
                estado = EstadoKirby.VOLAR;
                fly = true;
                body.setLinearVelocity(vel.x, VEL_FLY);

            }

        }
        else
        {
            fly = false;
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
            accion(delta);

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

    @Override
    public int getSalud(){
        return salud;
    }

    @Override
    public void quitarSalud(int puntos) {
        this.salud -= puntos;
    }

    @Override
    public void accion(float delta) {
        ArrayList<Entity> entidades = screen.getEntidades();
        Body body = CalculadoraDistancia.encontrarCuerpoMasCercano(this.body, this.world, 2f);
        if (body != null) {
            for (int i = 0; i < entidades.size(); i++) {
                Entity entidad = entidades.get(i);
                if (entidad.getBody() == body && entidad instanceof Enemy) {
                    Enemy enemy = (Enemy) entidad;
                    setPower(enemy.getUdata());

                    // Eliminar el cuerpo del enemigo del mundo Box2D
                    Array<Fixture> fixtures = new Array<>(body.getFixtureList());
                    for (Fixture fixture : fixtures) {
                        body.destroyFixture(fixture);
                    }
                    world.destroyBody(body);

                    // Eliminar del arraylist
                    entidades.remove(i);

                    // Eliminar la textura del enemigo del Stage
                    ((Actor)enemy).remove();

                    break;
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

    public void setOnWallRight(boolean colision) {
        this.onWallRight = colision;
    }

    public void setOnWallLeft(boolean onWallLeft) {
        this.onWallLeft = onWallLeft;
    }

    public void setCol(boolean col) {
        this.col = col;
    }

    public int getLastmove() {
        return lastmove;
    }

    public void setOnPlatform(boolean onPlatform) {
        this.onPlatform = onPlatform;
    }

    @Override
    public float getX(){
        return body.getPosition().x;
    }

    @Override
    public float getY(){
        return body.getPosition().y;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setPower(String uData){
        Rectangle rectangle = new Rectangle(getX(), getY(), getWidth(), getHeight());
        Kirby kirby = factory.createKirby(screen, body, rectangle, uData, salud);
        stage.addActor((Actor) kirby);
        screen.setKirby(kirby);
        screen.actReferencias(kirby);
        remove();
        //dispose();
    }

    @Override
    public Body getBody() {
        return body;
    }
}
