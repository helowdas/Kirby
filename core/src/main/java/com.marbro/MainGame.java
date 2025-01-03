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
        assetManager.load("music/musica2.ogg", Music.class);
        assetManager.load("entities/platform/platformWood.png", Texture.class);
        assetManager.load("entities/lever/0.png", Texture.class);
        assetManager.load("entities/block_Mecanismo/0.png", Texture.class);
        assetManager.load("entities/block_Mecanismo/1-(2).png", Texture.class);
        assetManager.load("entities/block_Mecanismo/1.png", Texture.class);
        assetManager.load("entities/block_Mecanismo/2.png", Texture.class);
        assetManager.load("entities/block_Mecanismo/3.png", Texture.class);
        assetManager.load("entities/block_Mecanismo/4.png", Texture.class);
        assetManager.load("entities/block_Mecanismo/5.png", Texture.class);
        assetManager.load("entities/BanderaWin/0.png", Texture.class);
        assetManager.load("entities/BanderaWin/1.png", Texture.class);
        assetManager.load("entities/BanderaWin/2.png", Texture.class);
        assetManager.load("entities/BanderaWin/3.png", Texture.class);
        assetManager.load("entities/BanderaWin/4.png", Texture.class);
        assetManager.load("entities/BanderaWin/5.png", Texture.class);
        assetManager.load("entities/BanderaWin/6.png", Texture.class);
        assetManager.load("entities/BanderaWin/7.png", Texture.class);
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
