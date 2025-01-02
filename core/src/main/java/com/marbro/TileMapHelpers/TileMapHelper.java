package com.marbro.TileMapHelpers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.marbro.entities.Platforms.PlatformHorizontal;
import com.marbro.entities.Platforms.PlatformVertical;
import com.marbro.entities.enemies.Sir_Kibble.Sir_Kibble;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee;
import com.marbro.entities.player.Kirby;
import com.marbro.screens.level1.Level1;
import com.marbro.entities.enemies.Factory.RandomEnemyFactory;
import static com.marbro.constants.Constantes.*;


public class TileMapHelper
{
        //atributos
        private TiledMap tiledMap;
        private Level1 gameScreen;
        private BodyHelperService bodyHelperService;
        RandomEnemyFactory randomEnemyFactory;

        //player
        private Kirby kirby;

        //fixture
        private Fixture fixture;

        //Tamaño mapa tiled
        private float mapWidth;
        private float mapHeight;

        public TileMapHelper(Level1 gameScreen)
        {
            this.gameScreen = gameScreen;
            this.bodyHelperService = new BodyHelperService();
            randomEnemyFactory = new RandomEnemyFactory();
        }

        public OrthogonalTiledMapRenderer setupMap()
        {
            tiledMap = new TmxMapLoader().load("map/level1/level1.tmx");
            mapWidth = tiledMap.getProperties().get("width", Integer.class) * PPM / 1;
            mapHeight = tiledMap.getProperties().get("height", Integer.class) * PPM / 1;

            //obtener los objetos del mapa
            parseMapObjects(tiledMap.getLayers().get("techo").getObjects(), "techo");
            parseMapObjects(tiledMap.getLayers().get("pinchos").getObjects(), "spike");
            parseMapObjects(tiledMap.getLayers().get("pared").getObjects(), "wall");
            parseMapObjects(tiledMap.getLayers().get("suelo").getObjects(), "block");
            parseMapObjects(tiledMap.getLayers().get("player").getObjects(), "player");
            parseMapObjects(tiledMap.getLayers().get("entities").getObjects(), "entity");
            return new OrthogonalTiledMapRenderer(tiledMap, 1/PPM);
        }

        //metodo para obtener objetos del tiled
        private void parseMapObjects(MapObjects mapObjects, String UserData)
        {
            for(MapObject mapObject: mapObjects)
            {
                if(mapObject instanceof PolygonMapObject)
                {
                    createStaticBody((PolygonMapObject)mapObject);
                }

                if(mapObject instanceof RectangleMapObject)
                {
                    Rectangle rectangle = ((RectangleMapObject)mapObject).getRectangle();
                    String rectangleName = mapObject.getName();

                    if(rectangleName != null)
                    {
                        if(rectangleName.equals("player"))
                        {
                            Body body = bodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth() / 2,
                                rectangle.getY() + rectangle.getHeight() / 2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                false, false,
                                gameScreen.getWorld()
                            );

                            kirby = new Kirby(gameScreen.getWorld(),
                                gameScreen.getStage(), body, gameScreen.getControlador(),
                                    rectangle.width, rectangle.height);

                            gameScreen.setKirby(kirby);
                        }

                        else if(rectangleName.equals("waddle_dee_jefe"))
                        {
                            Body body = bodyHelperService.createBody(
                                    rectangle.getX() + rectangle.getWidth() / 2,
                                    rectangle.getY() + rectangle.getHeight() / 2,
                                    rectangle.getWidth(),
                                    rectangle.getHeight(),
                                    false, false,
                                    gameScreen.getWorld()
                            );


                            gameScreen.getEntidades().add(new Waddle_dee(gameScreen.getWorld(), body,
                                    gameScreen.getControlador(), rectangle.width, rectangle.height, gameScreen.getKirby()));


                        }
                        else if(rectangleName.equals("Sir_Kibble"))
                        {
                            Body body = bodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth() / 2,
                                rectangle.getY() + rectangle.getHeight() / 2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                false, false,
                                gameScreen.getWorld()
                            );

                            gameScreen.getEntidades().add(new Sir_Kibble(gameScreen.getWorld(), body,
                                gameScreen.getControlador(), rectangle.width, rectangle.height, gameScreen.getKirby()));

                        }

                        else if(rectangleName.equals("enemigo")){
                            Body body = bodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth() / 2,
                                rectangle.getY() + rectangle.getHeight() / 2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                false, false,
                                gameScreen.getWorld()
                            );


                            gameScreen.getEntidades().add(randomEnemyFactory.createEnemy(gameScreen.getWorld(), body,
                                gameScreen.getControlador(), rectangle.width, rectangle.height, gameScreen.getKirby()));

                        }

                        else if (rectangleName.equals("platform"))
                        {
                            Body body = bodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth()/2,
                                rectangle.getY() + rectangle.getHeight()/2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                false, true,
                                gameScreen.getWorld()
                            );

                            gameScreen.getEntidades().add(new PlatformVertical(gameScreen.getWorld(), body,
                                rectangle.width, rectangle.height, false));

                        }
                        else if (rectangleName.equals("platform_invert"))
                        {
                            Body body = bodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth()/2,
                                rectangle.getY() + rectangle.getHeight()/2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                false, true,
                                gameScreen.getWorld()
                            );

                            gameScreen.getEntidades().add(new PlatformVertical(gameScreen.getWorld(), body,
                                rectangle.width, rectangle.height, true));

                        }
                        else if (rectangleName.equals("platform_horizontal"))
                        {
                            Body body = bodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth()/2,
                                rectangle.getY() + rectangle.getHeight()/2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                false, true,
                                gameScreen.getWorld()
                            );

                            gameScreen.getEntidades().add(new PlatformHorizontal(gameScreen.getWorld(), body,
                                rectangle.width, rectangle.height, false, DELAY));

                        }
                        else if (rectangleName.equals("platform_horizontal_invert"))
                        {
                            Body body = bodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth()/2,
                                rectangle.getY() + rectangle.getHeight()/2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                false, true,
                                gameScreen.getWorld()
                            );

                            gameScreen.getEntidades().add(new PlatformHorizontal(gameScreen.getWorld(), body,
                                rectangle.width, rectangle.height, true, DELAY));

                        }
                        else if (rectangleName.equals("platform_horizontal_S"))
                        {
                            Body body = bodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth()/2,
                                rectangle.getY() + rectangle.getHeight()/2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                false, true,
                                gameScreen.getWorld()
                            );

                            gameScreen.getEntidades().add(new PlatformHorizontal(gameScreen.getWorld(), body,
                                rectangle.width, rectangle.height, false, 2.5f));

                        }

                    }
                    else
                    {
                        Body body = bodyHelperService.createBody(
                                rectangle.getX() + rectangle.getWidth()/2,
                                rectangle.getY() + rectangle.getHeight()/2,
                                rectangle.getWidth(),
                                rectangle.getHeight(),
                                true, false,
                                gameScreen.getWorld()
                        );

                        body.getFixtureList().get(0).setUserData(UserData);

                    }

                }
            }
        }

        //metodo para crear las fixtures de los objetos
        private void createStaticBody(PolygonMapObject polygonMapObject)
        {
            //creacion del bodydef del objeto
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            //creacion del body del objeto
            Body body = gameScreen.getWorld().createBody(bodyDef);

            //creacion de fixture
            Shape shape = createPolygonShape(polygonMapObject);
            Fixture fixture = body.createFixture(shape, 10);
            fixture.setUserData("suelo");
            shape.dispose();
        }


        //creacion de los shapes de los objetos
        private Shape createPolygonShape(PolygonMapObject polygonMapObject)
        {
            float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
            Vector2[] worldVertices = new Vector2[vertices.length / 2];

            for(int i = 0; i < worldVertices.length; i++)
            {
                worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
            }

            PolygonShape shape = new PolygonShape();
            shape.set(worldVertices);
            return shape;
        }

        public void dispose()
        {
            tiledMap.dispose();
        }

        public float getMapWidth() {
            return mapWidth;
        }

        public float getMapHeight() {
            return mapHeight;
        }
}
