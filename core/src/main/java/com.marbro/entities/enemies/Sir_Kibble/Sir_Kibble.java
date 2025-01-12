package com.marbro.entities.enemies.Sir_Kibble;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;


import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;
import com.marbro.MainGame;
import com.marbro.animation.Animation_Base_Loop;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.contador.ActionTimer;
import com.marbro.entities.AnimationExplosion.AnimationExplosionEnemy;
import com.marbro.entities.enemies.Factory.Enemy;


import com.marbro.entities.enemies.waddle_dee.Waddle_dee;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee_Jefe;
import com.marbro.entities.player.Kirby;
import com.marbro.entities.player.kirby_base.Kirby_base;



import static com.marbro.constants.Constantes.*;

public class Sir_Kibble extends Actor implements Enemy {

    //Private animaciones
    private Animation_Base_Loop walk;
    private Animation_Base_Loop fall;
    private Animation_Base_Loop hurt;
    private AnimationHelperSirKibble animations;

    //Booleanos de estado
    private boolean jump;

    private World world;
    private Body body;
    private Fixture fixture;
    private Fixture fixture2;
    private float width;
    private float height;

    // Atributos para manejar el tiempo
    private long lastCollisionTime = 0;
    private static final long COLLISION_COOLDOWN = 1500; // 1.5 segundos en milisegundos

    //Estado del jugador
    private EstadoSirKibble estado;
    private int lastmove = -1;

    //Atributos del jugador
    private boolean life;
    private int salud; //atributos de salud del enemigo
    private boolean isAlive = true;

    //Run de Muerte
    protected Runnable dieRun;
    //animacion muerte
    protected AnimationExplosionEnemy animationExplosive;
    //sprite
    Sprite sprite;

    //Colisiones
    private boolean onGround;
    private boolean onSpike;
    private boolean onWall;
    private boolean colPlayer;

    //Controlador
    private Controlador_Colisiones controlador;
    private ColisionesSirKibble colisiones;

    //Contador
    private ActionTimer contador;
    private ActionTimer pain;


    public Sir_Kibble(World world, Body body,Controlador_Colisiones controlador, float width, float height, Kirby kirby)
    {
        this.world = world;
        this.body = body;
        body.getFixtureList().get(0).setUserData(this);
        this.width = width;
        this.height = height;

        animations = new AnimationHelperSirKibble();

        this.estado = EstadoSirKibble.CAYENDO;

        life = true;
        this.salud = 3;
        //cargar animacion
        this.animationExplosive = new AnimationExplosionEnemy(MainGame.getAssetManager());


        this.controlador = controlador;
        createContactListener(kirby);

        //definir Runnable
        defDie(this, controlador);

        contador = new ActionTimer();
        pain = new ActionTimer();
    }

    private void createContactListener(Kirby kirbyBase){
        colisiones = new ColisionesSirKibble(this,kirbyBase);
        controlador.addListener(colisiones);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(salud <= 0)
        {
            setAlive(false);
            muerte();
            sprite = animationExplosive.getFrameActual(delta);
        }

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
        if(isAlive)
        {
            batch.draw(frame, getX(), getY(), getWidth(), getHeight());
        }
        else
        {
            sprite.setPosition(getX(), getY());
            sprite.setSize(getWidth(), getHeight());
            sprite.draw(batch);
        }
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
            // Verificar si ha pasado suficiente tiempo desde la última colisión
            if (getTimeCollision()) {
                // Actualizar el tiempo de la última colisión
                lastCollisionTime = getCurrentTime();

                // Dirección de retroceso basada en la dirección actual del movimiento
                float forceX = lastmove == -1 ? 4.5f : -4.5f;
                float forceY = 3f;

                // Aplicar la fuerza al enemigo
                body.applyLinearImpulse(new Vector2(forceX, forceY), body.getWorldCenter(), true);


                pain.start();

            }
        }

        if (pain.getElapsedTime() > 1f) {
            enableCollisionWithPlayer(body);
            pain.reset();
            pain.pause();
        }
    }

    public void attack(){
        //Ataque del Sir_Kibble
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

    public String getUdata(){
        return (String) body.getFixtureList().get(0).getUserData();
    }

    @Override
    public void setHerido(boolean hurt) {
        System.out.println("g");
    }

    public void removeEnemy(){
        return;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public void muerte()
    {
        body.getFixtureList().get(0).setSensor(true);

        float timaAnimation = 0.3f;

        this.addAction(Actions.sequence(
            Actions.delay(timaAnimation), // Esperar la duración de la animación
            Actions.run(dieRun)
        ));
    }

    public void defDie(Sir_Kibble Sir_Kibble, Controlador_Colisiones controlador)
    {
        dieRun = new Runnable()
        {
            @Override
            public void run()
            {
                Sir_Kibble.remove();
                controlador.removeListener(Sir_Kibble.colisiones);
            }
        };
    }

    public void recibirDamage(int salud) {
        this.salud += salud;
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





    @Override
    public void actReferencia(Kirby kirby){
        colisiones.actReferencia(kirby);
    }

    public int getSalud() {
        return salud;
    }
}
