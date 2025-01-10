package com.marbro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.marbro.MainGame;

public class HelpScreen implements Screen {
    private MainGame game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Texture helpTexture;

    public HelpScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        helpTexture = new Texture(Gdx.files.internal("fondos/Ayuda.png"));
        stage = new Stage();

        // Carga de skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear el botón
        TextButton button = new TextButton("Volver al Menu", skin);
        button.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f-275);
        button.setSize(250, 75);

        // Añadir oyente al botón para que vuelva al menú
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(MainGame.getMenuScreen());
            }
        });

        // Añadir el botón al escenario
        stage.addActor(button);

        // Establecer la entrada procesada por el escenario
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(helpTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        helpTexture.dispose();
        stage.dispose();
    }
}
