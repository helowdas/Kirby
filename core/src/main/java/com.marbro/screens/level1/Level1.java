package com.marbro.screens.level1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.marbro.MainGame;
import com.marbro.TileMapHelpers.TileMapHelper;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.entities.enemies.Factory.EnemyFactory;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee;
import com.marbro.entities.player.Kirby;
import com.marbro.scenes.Hud;
import com.marbro.screens.GameOverScreen;

import static com.marbro.constants.Constantes.*;

public class Level1 implements Screen {
    //Atributos de camara y juego
    private MainGame game;
    private OrthographicCamera gamecame;
    private Viewport gameport;
    private Hud hud;

    //fondo
    private Texture texture;

    //Atributos de tiled y tmx
    //private TmxMapLoader maploader;
    //private TiledMap map;
    private TileMapHelper tileMapHelper;

    //camara del tiled
    private OrthogonalTiledMapRenderer renderer;

    //Ancho y alto del mundo
    private int anchoMundo, altoMundo;

    //Box 2d atributos
    private World world;
    private Box2DDebugRenderer b2dr;
    private Stage stage;

    //Musica del nivel
    private Music music;

    //Entidades del juego
    private Kirby kirby;
    private Waddle_dee waddle;

    //Animaciones
    private TextureAtlas atlas;

    //Tamaño mapa tiled
    private float mapWidth;
    private float mapHeight;

    //Clase controladora de colisiones
    private Controlador_Colisiones controlador;

    //atributos posicion Camara
    private float camX;
    private float camY;

    //atributos de animacion
    private Array<TextureRegion> walk;
    private Array<TextureRegion> stand;
    private Array<TextureRegion> fall1;
    private Array<TextureRegion> fall2;
    private Array<TextureRegion> jump;
    private Array<TextureRegion> abs;


    public Level1(MainGame game) {
        //Crear camara y hud
        this.game = game;
        gamecame = new OrthographicCamera();
        gameport = new FitViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM, gamecame);
        hud = new Hud(game.batch);

        //Creacion del world y el box2d
        world = new World(new Vector2(0,GRAVEDAD), true);
        b2dr = new Box2DDebugRenderer();
        stage = new Stage(gameport);
        Gdx.input.setInputProcessor(stage);

        //Esto es para manejar las colisiones a través de un contact listener
        controlador = new Controlador_Colisiones();
        world.setContactListener(controlador);


        //cargar tilemap
        this.tileMapHelper = new TileMapHelper(this);

        //Colocar y ajustar la cámara
        gamecame.zoom = ZOOM;
        gamecame.position.set(((Gdx.graphics.getWidth() * 0.64f) / (2f * PPM)),  (Gdx.graphics.getHeight() * 1f) / (2f * PPM), 0);


        //Cargar la musica
        music = game.getAssetManager().get("music/RandomLevel.ogg");

        //cargar animaciones

    }

    @Override
    public void show()
    {

        //cargar mapa
        this.renderer = tileMapHelper.setupMap();

        //Crear la entidad y añadirla al stage
        stage.addActor(kirby);

        //tomar posicion Y de la camara
        float y = kirby.getY() + kirby.getHeight() / 2 / PPM;
        camY = MathUtils.clamp(y, 5.7f, Gdx.graphics.getHeight() / PPM - gamecame.viewportHeight / 2/ PPM);

        //waddle = (Waddle_dee) EnemyFactory.createEnemy("Waddle_dee", world, stage, 10, 8, controlador);
        //stage.addActor(waddle);

        waddle = new Waddle_dee(world, stage, 10, 8, controlador);
        stage.addActor(waddle);



        //Para reproducir la música
        playMusic();
        music.setVolume(VOLUMEN);

    }


    public void update(float delta) {
        //Calculos para fijar los límites de la cámara (mejorarlos)
        float x = kirby.getX() + kirby.getWidth() / 2 / PPM;

        camX = MathUtils.clamp(x, gamecame.viewportWidth / PPM, (tileMapHelper.getMapWidth() - gamecame.viewportWidth)/ PPM);

        gamecame.position.set(camX, camY, 0);
        gamecame.update();
        renderer.setView(gamecame);
    }

    @Override
    public void render(float delta) {
        if (kirby.isAlive()){
            update(delta);

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            Gdx.gl.glClearColor(0,0.74f,0.8f,1);

            //Dibuja los elementos de tiled
            renderer.render();

            //Avanza la simulacion física de Box2d
            world.step(delta, 6, 2);

            //Actualizar y dibujar actores en el stage
            stage.act(delta);
            stage.draw();

            //Esta línea dibuja el body de box2d
            if (DEBUG)
            {
                b2dr.render(world, gamecame.combined);
            }

            //Establece una matriz de proyeccion que encaja con el HUD
            game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

            //Dibuja el HUD
            hud.stage.draw();
        } else {
            ((MainGame) Gdx.app.getApplicationListener()).setScreen(new GameOverScreen(game));
        }
    }

    @Override
    public void resize(int width, int height)
    {
        gameport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        stopMusic();
        kirby.detach();
        kirby.remove();
        if (waddle != null) {
            waddle.detach();
            waddle.remove();
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
        world.dispose();
        b2dr.dispose();
        stage.dispose();
    }

    private void playMusic() {
        music.setLooping(true); // Si quieres que la música se repita en bucle
        music.play();
    }

    private void stopMusic() {
        if (music.isPlaying()) {
            music.stop();
        }
    }

    //metodos gets

    public World getWorld() {
        return world;
    }

    public Stage getStage() {
        return stage;
    }

    public Controlador_Colisiones getControlador() {
        return controlador;
    }

    //set kirby
    public void setKirby(Kirby kirby) {
        this.kirby = kirby;
    }
}
