package com.marbro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.marbro.MainGame;

public class LoadingScreen implements Screen
{
    private MainGame mainGame;
    private Stage stage;
    private Skin skin;
    private Label loading;
    private int progress;
    private AssetManager assetManager;

    public LoadingScreen(MainGame mainGame, AssetManager assetManager)
    {
        this.assetManager = assetManager;
        this.mainGame = mainGame;
        FitViewport fitViewport= new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(fitViewport);

        // Carga de skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        //crear label
        loading = new Label("Loading...", skin);
        loading.setPosition(640 - loading.getWidth() / 2, 360 - loading.getHeight() / 2);
        stage.addActor(loading);
    }

    @Override
    public void render(float delta)
    {
        System.out.println(progress);
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        //ese metodo devuelve verdadero si todo se ha cargado
        if(assetManager.update())
        {
            mainGame.finishLoading();
        }
        else
        {
            //obtener el progreso de la carga del juego
            progress = (int) (assetManager.getProgress() * 100);
            loading.setText("loading... " + progress + " %");
        }

        stage.act(delta);
        stage.draw();
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
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose()
    {
        stage.dispose();
        skin.dispose();
    }
}
