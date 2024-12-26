package com.marbro.screens;

// Clase GameOverScreen
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class GameOverScreen implements Screen {
    private SpriteBatch batch;
    private Texture gameOverTexture;

    @Override
    public void show() {
        batch = new SpriteBatch();
        gameOverTexture = new Texture(Gdx.files.internal("fondos/gameover.png")); // Asegúrate de tener una imagen de Game Over
    }

    @Override
    public void render(float delta) {
        // Limpiar la pantalla con un color negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Dibuja la pantalla de Game Over
        batch.draw(gameOverTexture, Gdx.graphics.getWidth() / 2f - gameOverTexture.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - gameOverTexture.getHeight() / 2f);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

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
    }
}
