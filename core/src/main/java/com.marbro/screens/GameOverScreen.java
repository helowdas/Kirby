package com.marbro.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.marbro.MainGame;

public class GameOverScreen implements Screen {
    private SpriteBatch batch;
    private Texture gameOverTexture;
    private Stage stage;
    private Skin skin;
    private MainGame game;

    public GameOverScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        gameOverTexture = new Texture(Gdx.files.internal("fondos/gameover.png"));
        stage = new Stage();

        // Carga de skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear el botón
        TextButton button = new TextButton("Volver al Menu", skin);
        button.setPosition(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f - 400);
        button.setSize(400, 100);

        // Añadir oyente al botón para que vuelva al menú
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
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
        batch.draw(gameOverTexture, Gdx.graphics.getWidth() / 2f - gameOverTexture.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - gameOverTexture.getHeight() / 2f);
        batch.end();

        // Renderizar el escenario
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        gameOverTexture.dispose();
        stage.dispose();
        skin.dispose();
    }
}
