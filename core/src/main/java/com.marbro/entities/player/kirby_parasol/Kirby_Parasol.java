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
import com.marbro.entities.player.kirby_base.ColisionesHandlerKirby;
import com.marbro.entities.player.kirby_base.EstadoKirby;
import com.marbro.scenes.Hud;
import com.marbro.screens.level1.Level1;
import com.marbro.sounds.SoundHelperKirby;

import java.util.ArrayList;

import static com.marbro.constants.Constantes.*;
import static com.marbro.entities.player.kirby_base.Kirby_base.puntuacion;

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
    public boolean onGround = true;
    public boolean onSpike = false;
    public boolean onWallRight = false;
    public boolean onWallLeft = false;
    public boolean onPlatform = false;
    public boolean fly = false;
    public boolean col = false;
    public boolean isWin = false;
    public boolean isAttack = false;
    public boolean isBossDefeat = false;

    //Controlador de colisiones
    private Controlador_Colisiones controlador;
    private FixtureDef ataque;
    private Fixture area;

    //Atributos del jugador
    private boolean life;
    private int salud;

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

    //controlador de coliones kirby
    ColisionesHandlerKirby colisionesHandlerKirby;


    public Kirby_Parasol(World world, Stage stage, Body body, Controlador_Colisiones controlador, float width, float height,
                  Level1 screen, int salud, ColisionesHandlerKirby colisionesHandlerKirby)
    {
        this.screen = screen;

        this.world = world;
        this.stage = stage;
        this.body = body;
        body.getFixtureList().get(0).setUserData(this);
        this.width = width;
        this.height = height;
        this.colisionesHandlerKirby = colisionesHandlerKirby;

        this.estado = EstadoKirbyParasol.QUIETO;
        life = true;

        cal = new CalculadoraDistancia();

        contador = new ActionTimer();

        animations = new AnimationHelperKirbyParasol();

        this.controlador = controlador;
        //createContactListener();

        this.screen = screen;

        sounds = new SoundHelperKirby();

        this.salud = salud;

        factory = new FactoryKirby();

    }


    @Override
    public void act(float delta)
    {
        Hud.actScore(puntuacion);
        super.act(delta);
        if (contador != null)
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


    public int getSalud() {
        return salud;
    }

    @Override
    public void quitarSalud(int damage_points) {
        this.salud -= damage_points;
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
        setPower(null);
        verificarCondicionesEspeciales(vel);
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
            estado = EstadoKirbyParasol.CAMINANDO;
            this.isAttack = false;
        }
    }

    private void mantenerseQuieto(Vector2 vel) {
        if (estado != EstadoKirbyParasol.ATACANDO) {
            body.setLinearVelocity(0f, vel.y);
            if(body.getLinearVelocity().y == 0){
                estado = EstadoKirbyParasol.QUIETO;
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

        if (estado != EstadoKirbyParasol.ATACANDO && body.getLinearVelocity().y > 0 && !onPlatform && !fly) {
            estado = EstadoKirbyParasol.SALTANDO;
            this.isAttack = false;
        }

    }

    private void manejarEstadoEnCaida(Vector2 vel) {
        if (estado != EstadoKirbyParasol.ATACANDO) {
            if (jump && -3.5f < vel.y && vel.y < 0f) {
                estado = EstadoKirbyParasol.CAYENDO;
                this.isAttack = false;
            } else {
                if (vel.y < 0 && !onGround) {
                    estado = EstadoKirbyParasol.CAYENDO;
                } else if (-0.5f < vel.y && vel.y < 0f) {
                    estado = EstadoKirbyParasol.CAYENDO;
                } else if (vel.y > -0.5 && vel.y <= 0f && !onGround) {
                    estado = EstadoKirbyParasol.CAYENDO;
                }
            }
        }
    }

    private void ajustarEstadoQuieto(Vector2 vel)
    {
        if (vel.x == 0 && vel.y == 0 && onGround && estado != EstadoKirbyParasol.ATACANDO) {
            estado = EstadoKirbyParasol.QUIETO;
            jump = false; // Resetear salto al tocar el suelo
        }
        else if (onPlatform && vel.x == 0 && estado != EstadoKirbyParasol.ATACANDO)
        {
            estado = EstadoKirbyParasol.QUIETO;
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
                estado = EstadoKirbyParasol.VOLAR;
                fly = true;
                body.setLinearVelocity(vel.x, VEL_FLY);

            }

        }
        else
        {
            fly = false;
        }

    }

    public void setPower(Enemy enemy){
        if (col){
            Rectangle rectangle = new Rectangle(getX(), getY(), 20f, 20f);
            Kirby kirby = factory.createKirby(screen, body, rectangle, null, salud, this.colisionesHandlerKirby);
            stage.addActor((Actor) kirby);
            screen.setKirby(kirby);
            screen.actReferencias(kirby);
            remove();
        }
    }

    @Override
    public void accion(float delta) {
        // Gestionar delay entre aspiraciones
        if (attackDelayTimer > 0) {
            attackDelayTimer -= delta;
        }

        // Iniciar aspiración si el delay ha terminado
        if (Gdx.input.isKeyPressed(Input.Keys.F) && attackDelayTimer <= 0 &&
            estado != EstadoKirbyParasol.PLANEANDO &&
            estado != EstadoKirbyParasol.VOLAR &&
            estado != EstadoKirbyParasol.PREVOLAR
        ) {
            if (estado != EstadoKirbyParasol.ATACANDO && estado != EstadoKirbyParasol.CAYENDO) {
                estado = EstadoKirbyParasol.ATACANDO;
                this.isAttack = true;
                sounds.playSound("attack");
                attackTimer = 0; // Reiniciar el temporizador de la animación
                animations.resetAnimation(estado); // Reseteo al iniciar ataque
            }
        }

        // Continuar la animación de ataque aunque se suelte la tecla F
        if (estado == EstadoKirbyParasol.ATACANDO) {
            attackTimer += delta;
            ataque(); // Continuar ataque

            // Detener la aspiración si ha alcanzado la duración máxima
            if (attackTimer >= ATTACK_DURATION) {
                estado = EstadoKirbyParasol.QUIETO;
                this.isAttack = false;
                attackTimer = 0; // Reiniciar el temporizador de la animación
                attackDelayTimer = ATTACK_DELAY; // Iniciar timer de delay
                animations.resetAnimation(estado); // Reseteo al terminar ataque
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
             posY = (body.getPosition().y ) - (getHeight() / 2) + 0.15f;
        }

        if (frame.getRegionWidth() == 128){
             posX = (body.getPosition().x) - (getWidth());
             posY = (body.getPosition().y ) - (getHeight() / 2) + 0.60f;
        }

        setPosition(posX, posY);
    }

    public Body getBody() {
        return body;
    }

    @Override
    public boolean isWin() {
        return isWin;
    }

    @Override
    public void setWin(boolean win) {
        this.isWin = win;
    }

    public static int getLastMove(){
        return lastmove;
    }

    public void actReferencias(Kirby kirby){
        screen.actReferencias(kirby);
    }

    @Override
    public boolean getAttack() {
        return isAttack;
    }

    public void createAttack()
    {
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() * 2, getHeight()/2);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        area = body.createFixture(fixtureDef);
        area.setUserData("attack");
        shape.dispose();
    }

    @Override
    public boolean isBossDefeat() {
        return isBossDefeat;
    }

    @Override
    public void setBossDefeat(boolean bossDefeat) {
        isBossDefeat = bossDefeat;
    }
}


