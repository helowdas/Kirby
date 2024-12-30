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
import com.marbro.entities.enemies.Sir_Kibble.Sir_Kibble;
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
    private Sir_Kibble sir_kibble;
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


        //Ejemplo de como se carga una animacion
        walk = new Array<>();
        loadTexture(walk, "entities/player/kirby_walk/kirby_walk_", 1, 10);

        stand = new Array<>();
        loadTexture(stand, "entities/player/kirby_stand/kirby_stand_", 1, 1);

        fall1 = new Array<>();
        loadTexture(fall1, "entities/player/kirby_fall/kirby_fall_", 1, 7);

        fall2 = new Array<>();
        loadTexture(fall2,"entities/player/kirby_fall/kirby_fall_" , 8, 9);

        jump = new Array<>();
        loadTexture(jump, "entities/player/kirby_jump/kirby_jump_", 1, 1);

        abs = new Array<>();
        loadTexture(abs, "entities/player/kirby_abs/kirby_abs_", 1, 7);

        //cargar tilemap
        this.tileMapHelper = new TileMapHelper(this);
        this.renderer = tileMapHelper.setupMap();

        //colisiones = new Colisiones();
        //world.setContactListener(colisiones);

        //Cargar el mapa creado en tiled
        //loadMap("map/level1/level1.tmx");

        //Colocar y ajustar la cámara
        gamecame.zoom = ZOOM;
        gamecame.position.set(((Gdx.graphics.getWidth() * 0.64f) / (2f * PPM)),  (Gdx.graphics.getHeight() * 1f) / (2f * PPM), 0);

        //Cargar bloques creados en el tiled
        /*loadBlocks(1, "block", CATEGORY_BLOCK, CATEGORY_PLAYER, CATEGORY_ENEMY);
        loadBlocks(2, "wall", CATEGORY_WALL, CATEGORY_PLAYER, CATEGORY_ENEMY);
        loadBlocks(3, "spikes", CATEGORY_SPIKE, CATEGORY_PLAYER, CATEGORY_ENEMY);*/

        world.setContactListener(controlador);

        //Cargar la musica
        music = game.getAssetManager().get("music/RandomLevel.ogg");

        //cargar animaciones

    }

    /*
    loadMap: Sirve para cargar el mapa de tiled a libgdx
    @Param path: es la ruta y nombre del mapa a cargar
     */
    /*public void loadMap(String path){
        maploader = new TmxMapLoader();
        map = maploader.load(path);
        mapWidth = map.getProperties().get("width", Integer.class) * PPM / 1;
        mapHeight = map.getProperties().get("height", Integer.class) * PPM / 1;
        renderer = new OrthogonalTiledMapRenderer(map, 1 / PPM);
    }


    loadBlocks: sirve para añadir a box2d los objetos creados en tiled
    @Param layer: es el id de capa que tienen los objetos dentro de tiled, empieza desde 0

    public void loadBlocks(int layer, String uData, short category, short coll1, short coll2){
        //Creacion del bodydef
        BodyDef bdef = new BodyDef(); //Se encarga de definir posición y tipo de body

        PolygonShape shape; //Se encarga de representar la figura del body

        FixtureDef fdef = new FixtureDef(); //Se encarga de representar atributos como densidad , fricción , rebote y forma a través de shape

        Body body; //Engloba a bodydef y fixture

        //cargar bloques físicos de libgdx
        for (MapObject object : map.getLayers().get(layer).getObjects().getByType(RectangleMapObject.class) ) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;

            // Ajuste de la posición considerando la conversión correcta
            float posX = (rect.getX() + rect.getWidth() / 2) / PPM;
            float posY = (rect.getY() + rect.getHeight() / 2) / PPM;

            bdef.position.set(posX, posY);

            body = world.createBody(bdef);

            shape = new PolygonShape();

            float width = rect.getWidth() / PPM;
            float height = rect.getHeight() / PPM;

            shape.setAsBox(width / 2, height / 2); // Asegúrate de usar width / 2 y height / 2 para crear el rectángulo

            fdef.shape = shape;

            //Cada fixture de los bloques tiene una etiqueta que será utilizada para manejar colisiones
            fdef.filter.categoryBits = category;

            //fdef.filter.maskBits = (short) (coll1 | coll2);

            //Se crea el fixture y se le asigna un userData
            Fixture fixture =  body.createFixture(fdef);
            fixture.setUserData(uData);

            shape.dispose(); // Asegúrate de liberar los recursos
        }
    }*/

    @Override
    public void show()
    {
        //Ejemplo de como se carga una animacion
        /*walk = new Array<>();
        loadTexture(walk, "entities/player/kirby_walk/kirby_walk_", 1, 10);

        stand = new Array<>();
        loadTexture(stand, "entities/player/kirby_stand/kirby_stand_", 1, 1);

        fall1 = new Array<>();
        loadTexture(fall1, "entities/player/kirby_fall/kirby_fall_", 1, 7);

        fall2 = new Array<>();
        loadTexture(fall2,"entities/player/kirby_fall/kirby_fall_" , 8, 9);

        jump = new Array<>();
        loadTexture(jump, "entities/player/kirby_jump/kirby_jump_", 1, 1);

        abs = new Array<>();
        loadTexture(abs, "entities/player/kirby_abs/kirby_abs_", 1, 7);*/

        //cargar mapa
        //this.renderer = tileMapHelper.setupMap();

        //Crear la entidad y añadirla al stage
        stage.addActor(kirby);

        //tomar posicion Y de la camara
        float y = kirby.getY() + kirby.getHeight() / 2 / PPM;
        camY = MathUtils.clamp(y, 5.7f, Gdx.graphics.getHeight() / PPM - gamecame.viewportHeight / 2/ PPM);

        //waddle = (Waddle_dee) EnemyFactory.createEnemy("Waddle_dee", world, stage, 10, 8, controlador);
        //stage.addActor(waddle);

        waddle = new Waddle_dee(world, stage, 10, 8, controlador);
        stage.addActor(waddle);

        sir_kibble = new Sir_Kibble(world, stage, 10, 8, controlador);
        stage.addActor(sir_kibble);





        //Para reproducir la música
        playMusic();
        music.setVolume(VOLUMEN);

    }

    /*
    loadTexture: sirve para cargar las texturas en un arreglo de texture region
    @Param animation: es el array que va a contener los frames
    @Param path: es la ruta donde se encuentran las imagenes, solo la ruta no hay que poner el .png
    @Param cant_frames: es la cantidad de frames que tiene
    IMPORTANTE: LOS PNG DEBEN ESTAR ENUMERADOS, EMPEZANDO DESDE EL 1 (kirby1.png, kiby2.png ...)
    */
    public void loadTexture(Array <TextureRegion> animation, String path, int i, int f){
        for (int k = i; i <= f; i++){
            Texture texture = new Texture(Gdx.files.internal(path + i + ".png"));
            animation.add(new TextureRegion(texture));
        }
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

    //animaciones
    public Array<TextureRegion> getWalk() {
        return walk;
    }

    public Array<TextureRegion> getFall1() {
        return fall1;
    }

    public Array<TextureRegion> getFall2() {
        return fall2;
    }

    public Array<TextureRegion> getJump() {
        return jump;
    }

    public Array<TextureRegion> getStand() {
        return stand;
    }

    public Array<TextureRegion> getAbs() {
        return abs;
    }

    //set kirby

    public void setKirby(Kirby kirby) {
        this.kirby = kirby;
    }
}
