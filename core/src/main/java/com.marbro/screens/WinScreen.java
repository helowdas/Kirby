package com.marbro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.marbro.MainGame;
import com.marbro.ranking.Ranking;
import com.marbro.screens.level1.Level1;

import javax.swing.*;

import static com.marbro.MainGame.sesionEstado;
import static com.marbro.MainGame.usuario;
import static com.marbro.entities.player.kirby_base.Kirby_base.puntuacion;
import static com.marbro.scenes.Hud.resetTimer;

public class WinScreen implements Screen {
    private MainGame game;
    private Stage stage;
    private Texture kirby_imagen;
    private Texture fondo_cielo;
    private Texture imagen_copa;
    private SpriteBatch batch;

    public WinScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        if (sesionEstado){
            Ranking.getInstance().modificarPuntuacion(usuario, puntuacion);
        } else {
            JOptionPane.showMessageDialog(null, "No ha iniciado sesión, la puntuacion no sera guardada :(");
        }
        puntuacion = 0;

        batch = new SpriteBatch();
        kirby_imagen = new Texture(Gdx.files.internal("fondos/kirby_imagen_ganar.png"));
        fondo_cielo = new Texture(Gdx.files.internal("fondos/fondo_cielo.png"));
        imagen_copa = new Texture(Gdx.files.internal("fondos/imagen_ganar.png"));
        stage = new Stage();


        // Carga de skin
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear el botón
        TextButton button = new TextButton("Volver al Menu", skin);
        button.setPosition(Gdx.graphics.getWidth() / 2f - 250, Gdx.graphics.getHeight() / 2f - 225);

        button.setSize(200, 75);

        TextButton button2 = new TextButton("Jugar de nuevo", skin);
        button2.setPosition(Gdx.graphics.getWidth() / 2f + 50, Gdx.graphics.getHeight() / 2f - 225);
        button2.setSize(200, 75);

        // Añadir oyente al botón para que vuelva al menú
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(MainGame.getMenuScreen());
            }
        });

        button2.addListener((new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(MainGame.getLevel1());
            }
        }));

        // Añadir el botón al escenario
        stage.addActor(button);
        stage.addActor(button2);

        // Establecer la entrada procesada por el escenario
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta)
        {
        if (sesionEstado){
            Ranking.getInstance().guardarPuntuaciones(usuario, puntuacion);
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        resetTimer();

        batch.begin();
        batch.draw(fondo_cielo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(kirby_imagen, Gdx.graphics.getWidth() / 2f - 125, Gdx.graphics.getHeight() / 2f + 50, 300, 200);
        batch.draw(imagen_copa, Gdx.graphics.getWidth() / 2f - 125, Gdx.graphics.getHeight() / 2f - 150, 250, 200); // Tamaño reducido y posición ajustada
        batch.end();

        stage.act();
        stage.draw();
        }

    @Override
    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {Gdx.input.setInputProcessor(null);}
    @Override
    public void dispose() {
        batch.dispose();
        kirby_imagen.dispose();
        fondo_cielo.dispose();
        imagen_copa.dispose();
        stage.dispose();
    }


}
