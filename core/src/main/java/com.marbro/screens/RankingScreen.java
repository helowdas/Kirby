package com.marbro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.marbro.MainGame;
import com.marbro.ranking.Ranking;

import java.util.List;

import static com.marbro.MainGame.getMenuScreen;
import static com.marbro.MainGame.usuario;

public class RankingScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private Ranking ranking;
    private Stage stage;
    private MainGame game;

    public RankingScreen(Ranking ranking, MainGame game) {
        this.ranking = ranking;
        batch = new SpriteBatch();
        font = new BitmapFont();
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        //helpTexture = new Texture(Gdx.files.internal("fondos/Ayuda.png")); fondo de ranking
        stage = new Stage();

        // Carga de skin
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear el botón
        TextButton button = new TextButton("Volver al Menu", skin);
        button.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f-275);
        button.setSize(250, 75);

        // Boton de registrarse
        TextButton registrarse = new TextButton("Registrarse / Iniciar Sesion ", skin);
        registrarse.setPosition(Gdx.graphics.getWidth() / 2f + 50, Gdx.graphics.getHeight() / 2f-275 + 100);
        registrarse.setSize(200, 75);

        // Boton de eliminar jugador
        TextButton eliminar = new TextButton("Eliminar jugador", skin);
        eliminar.setPosition(Gdx.graphics.getWidth() / 2f + 50, Gdx.graphics.getHeight() / 2f-275 + 200);

        TextButton cambiar = new TextButton("Cambiar nombre", skin);
        cambiar.setPosition(Gdx.graphics.getWidth() / 2f + 50, Gdx.graphics.getHeight() / 2f-275 + 300);

        TextButton cerrar_sesion = new TextButton("Cerrar sesion", skin);
        cerrar_sesion.setPosition(Gdx.graphics.getWidth() / 2f + 50, Gdx.graphics.getHeight() / 2f-275 + 400);

        // Añadir oyente al botón para que vuelva al menú
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getMenuScreen().updatePlayerNameLabel();
                game.setScreen(MainGame.getMenuScreen());
            }
        });

        registrarse.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                usuario = Ranking.getInstance().registrarJugador();
            }
        });

        eliminar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ranking.getInstance().eliminarJugador();
            }
        });

        cambiar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ranking.getInstance().cambiarNombreJugador(usuario);
            }
        });

        cerrar_sesion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Ranking.getInstance().cerrarSesion();
            }
        });

        // Añadir el botón al escenario
        stage.addActor(button);
        stage.addActor(eliminar);
        stage.addActor(registrarse);
        stage.addActor(cambiar);
        stage.addActor(cerrar_sesion);

        // Crear el label con la información para colocar el nombre
        Label labelInfo = new Label("El nombre del jugador debe empezar con una letra.\n" +
                "Caracteres permitidos: letras, numeros y un espacio entre palabras.", skin);
        labelInfo.setPosition(0, Gdx.graphics.getHeight() - 500);
        labelInfo.setSize(100, 500);
        labelInfo.setAlignment(Align.topLeft);

        // Añadir el label al escenario
        stage.addActor(labelInfo);

        // Establecer la entrada procesada por el escenario
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Limpieza de la pantalla con color azul
        Gdx.gl.glClearColor(0, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        List<String> listaRanking = ranking.obtenerRanking();

        // Mostrar las puntuaciones en la pantalla
        for (int i = 0; i < listaRanking.size(); i++) {
            String texto = (i + 1) + ". " + listaRanking.get(i);
            font.draw(batch, texto, 100, 400 - i * 20, 1, Align.left, false);
        }

        stage.act();
        stage.draw();

        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
