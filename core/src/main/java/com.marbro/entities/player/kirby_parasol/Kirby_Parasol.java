package com.marbro.entities.player.kirby_parasol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.marbro.TileMapHelpers.BodyHelperService;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.contador.ActionTimer;
import com.marbro.distance.CalculadoraDistancia;
import com.marbro.entities.enemies.Factory.Enemy;
import com.marbro.entities.player.Kirby;
//import com.marbro.entities.EntitiesFactory;
import com.marbro.entities.player.FactoryKirby;
import com.marbro.entities.player.kirby_base.AnimationHelperKirby;
import com.marbro.entities.player.kirby_base.EstadoKirby;
import com.marbro.screens.level1.Level1;
import com.marbro.sounds.SoundHelperKirby;

import java.util.ArrayList;

import static com.marbro.constants.Constantes.*;

public class Kirby_Parasol extends Actor implements Kirby {
    //Animaciones
    private AnimationHelperKirbyParasol animations;

    //Atributos de box2d
    private World world;
    private Body body;
    private Stage stage;
    private Fixture fixture;
    private FixtureDef fixDef;

    //Estado del jugador
    private EstadoKirbyParasol estado;
    private static int lastmove = -1;
    private boolean jump;
    private boolean attacking;

    //Colisiones del jugador
    public boolean onGround = false;
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

    //Calculadora distancia
    CalculadoraDistancia cal;

    //Contador
    ActionTimer contador;
    private static final float ATTACK_DURATION = 0.285f;
    private static final float ATTACK_DELAY = 0.5f;
    private float attackTimer = 0;
    private float attackDelayTimer = 0;

    //Sonidos
    private SoundHelperKirby sounds;

    //Posicion
    private float posX, posY;

    //Factory
    private FactoryKirby factory;

    //Screen
    private Level1 screen;

    //Otros
    private float width, height;

    public Kirby_Parasol(World world, Stage stage, Body body, Controlador_Colisiones controlador, float width, float height,
                  Level1 screen)
    {
        this.screen = screen;

        this.world = world;
        this.stage = stage;
        this.body = body;
        body.getFixtureList().get(0).setUserData(this);
        this.width = width;
        this.height = height;

        this.estado = EstadoKirbyParasol.QUIETO;
        life = true;


        cal = new CalculadoraDistancia();

        contador = new ActionTimer();

        animations = new AnimationHelperKirbyParasol();

        this.controlador = controlador;
        createContactListener();

        this.screen = screen;

        sounds = new SoundHelperKirby();
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
        fixDef = new FixtureDef();
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
        ColisionesHandlerKirbyParasol colisionesHandler = new ColisionesHandlerKirbyParasol(this);
        controlador.addListener(colisionesHandler);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (contador != null)
            if (!contador.isRunning()){
                contador.start();
            }

        animations.update(delta);
        updatePlayerState(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        // Ajustar el tamaño del actor en píxeles, considerando PPM

        TextureRegion frame = drawKirby();
        ajustarSize(frame);
        ajustarPosicion(frame);

        float width = getWidth() * 2;
        float height = getHeight() * 2;

        batch.draw(frame, getX(), getY(), width, height);
    }

    @Override
    public void detach()
    {
        if (fixture != null) {
            body.destroyFixture(fixture);
            fixture = null;
        }

        if (body != null) {
            world.destroyBody(body);
            body = null;
        }

        contador = null;
        this.remove();
    }

    public void dispose(){
        sounds.dispose();
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
        if (contador.getElapsedTime() > 0.2f)
            accion(delta);
        planear(delta);
        hurt();
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

        if (vel.x != 0 && onGround && estado != EstadoKirbyParasol.ATACANDO) {
            estado = EstadoKirbyParasol.CAMINANDO;
        }
    }

    private void moverDerecha(Vector2 vel) {
        body.setLinearVelocity(VELOCIDAD, vel.y);
        lastmove = 1;
        if (onGround && onPlatform && !jump && estado != EstadoKirbyParasol.ATACANDO) {
            estado = EstadoKirbyParasol.CAMINANDO;
        }
    }

    private void moverIzquierda(Vector2 vel) {
        body.setLinearVelocity(-VELOCIDAD, vel.y);
        lastmove = -1;
        if (onGround && onPlatform && !jump && estado != EstadoKirbyParasol.ATACANDO) {
            estado = EstadoKirbyParasol.ATACANDO;
        }
    }

    private void mantenerseQuieto(Vector2 vel) {
        if (onGround && !jump && estado != EstadoKirbyParasol.ATACANDO) {
            body.setLinearVelocity(0f, vel.y);
            estado = EstadoKirbyParasol.QUIETO;
        }
    }

    private void manejarSalto(Vector2 vel) {
        if (onGround && Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (estado != EstadoKirbyParasol.ATACANDO){
                estado = EstadoKirbyParasol.SALTANDO;
            }
            body.setLinearVelocity(vel.x, 5f);
            sounds.playSound("jump");
            jump = true;
        }
    }

    private void manejarEstadoEnCaida(Vector2 vel) {
        if (estado != EstadoKirbyParasol.ATACANDO) {
            if (vel.y < 0f) {
                estado = EstadoKirbyParasol.CAYENDO;
            }
        }
    }

    private void ajustarEstadoQuieto(Vector2 vel) {
        if (vel.x == 0 && vel.y == 0 && onGround && estado != EstadoKirbyParasol.ATACANDO) {
            estado = EstadoKirbyParasol.QUIETO;
            jump = false;
        }
    }

    public void hurt(){
        if (col){
            BodyHelperService helper = new BodyHelperService();
            Body body = helper.createBody(getX(), getY(), getWidth(), getHeight(), false, false, world);
            Rectangle rectangle = new Rectangle(getX(), getY(), getWidth(), getHeight());
            Kirby kirby = factory.createKirby(screen, body, rectangle, "kirby");
            detach();
            dispose();
        }
    }

    @Override
    public void accion(float delta) {
        // Gestionar delay entre aspiraciones
        if (attackDelayTimer > 0) {
            attackDelayTimer -= delta;
        }

        // Iniciar aspiración si el delay ha terminado
        if (Gdx.input.isKeyPressed(Input.Keys.F) && attackDelayTimer <= 0) {
            if (estado != EstadoKirbyParasol.ATACANDO) {
                estado = EstadoKirbyParasol.ATACANDO;
                attackTimer = 0; // Reiniciar el temporizador de la animación
                animations.resetAnimation(estado); // Reseteo al iniciar ataque
            }
            attackTimer += delta;
            ataque();

            // Detener la aspiración si ha alcanzado la duración máxima
            if (attackTimer >= ATTACK_DURATION) {
                estado = EstadoKirbyParasol.QUIETO;
                attackTimer = 0;  // Reiniciar el temporizador de la animación
                attackDelayTimer = ATTACK_DELAY; // Iniciar timer de delay
                animations.resetAnimation(estado); // Reseteo al terminar ataque
            }
        } else if (estado == EstadoKirbyParasol.ATACANDO) {
            attackDelayTimer = ATTACK_DELAY; // Asegurar el delay solo se reinicia si se detuvo el ataque

            // Mantener la animación mientras se presiona la tecla
            if (!Gdx.input.isKeyPressed(Input.Keys.F)) {
                estado = EstadoKirbyParasol.QUIETO;
                attackTimer = 0; // Reiniciar el temporizador y poner en espera
                animations.resetAnimation(estado); // Reseteo al detener ataque
            }
        }
    }

    private void planear(float delta){
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && estado == EstadoKirbyParasol.CAYENDO) {
            body.setGravityScale(0.07f); // Reduce la gravedad
            // Establece una velocidad constante mientras está planeando
            body.setLinearVelocity(body.getLinearVelocity().x, -1.0f);
            estado = EstadoKirbyParasol.PLANEANDO;
        } else {
            body.setGravityScale(1.0f); // Gravedad normal
        }
    }

    public void ataque() {
        // Marca que Kirby está en modo de ataque
        attacking = true;
        Enemy enemy = null;

        // Establece el punto de ataque basado en la dirección
        Vector2 attackPoint = new Vector2(body.getPosition()).add( 0.5f * lastmove, 0);

        // Crea un área pequeña alrededor del punto de ataque para detectar colisiones
        PolygonShape attackZone = new PolygonShape();
        attackZone.setAsBox(0.5f, 0.5f, attackPoint, 0);

        // Verifica colisiones en el área de ataque
        ArrayList<Fixture> fixtures = new ArrayList<>();
        world.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                fixtures.add(fixture);
                return true;
            }
        }, attackPoint.x - 0.5f, attackPoint.y - 0.5f, attackPoint.x + 0.5f, attackPoint.y + 0.5f);

        // Aplica daño a los enemigos dentro del área de ataque
        for (Fixture f : fixtures) {
            if (f.getUserData() instanceof Enemy) {
                enemy = (Enemy) f.getUserData();
                enemy.setHerido(true);
            }
        }

        // Resetea el estado de ataque
        attacking = false;
        attackZone.dispose();
    }


    private void verificarCondicionesEspeciales() {
        if (onSpike || body.getPosition().y < -8) {
            life = false;
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
    public void ajustarSize(TextureRegion frame) {
        setSize(frame.getRegionWidth() / 64f, frame.getRegionHeight() / 64f);
    }

    public void ajustarPosicion(TextureRegion frame) {
        if (frame.getRegionWidth() == 64 ){
            // Ajustar la posicion del sprite
             posX = (body.getPosition().x) - (getWidth());
             posY = (body.getPosition().y ) - (getHeight() / 2) + 0.25f;
        }

        if (frame.getRegionWidth() == 128){
             posX = (body.getPosition().x) - (getWidth());
             posY = (body.getPosition().y ) - (getHeight() / 2) + 0.75f;
        }

        setPosition(posX, posY);
    }

    public Body getBody() {
        return body;
    }

    public static int getLastMove(){
        return lastmove;
    }
}


