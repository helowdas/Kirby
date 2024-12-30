package com.marbro;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.marbro.screens.MenuScreen;

public class MainGame extends Game
{
    public SpriteBatch batch;
    private static AssetManager assetManager = new AssetManager();

    @Override
    public void create()
    {
        //Cargar assets
        assetManager.load("music/RandomLevel.ogg", Music.class);
        assetManager.finishLoading(); //assets cargados

        batch = new SpriteBatch();
        setScreen(new MenuScreen(   this));
    }

    @Override
    public void render()
    {
        super.render();
    }

    @Override
    public void dispose()
    {
        batch.dispose();
        assetManager.dispose();
    }

    public static AssetManager getAssetManager()
    {
        return assetManager;
    }
}
