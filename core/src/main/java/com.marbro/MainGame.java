package com.marbro;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.marbro.ranking.Ranking;
import com.marbro.screens.*;
    import com.marbro.screens.level1.Level1;

public class MainGame extends Game
{
    public SpriteBatch batch;
    private static AssetManager assetManager = new AssetManager();
    private static MenuScreen menuScreen;
    private static GameOverScreen gameOverScreen;
    private static WinScreen winScreen;
    private static HelpScreen helpScreen;
    private static AboutScreen aboutScreen;
    private LoadingScreen loadingScreen;
    private static Level1 level1;
    private static RankingScreen rankingScreen;

    public static String usuario = " registrese";
    public static int puntuacion_archivos = 0;
    public static boolean sesionEstado = false;

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
        assetManager.load("entities/explosion_enemy/0.png", Texture.class);
        assetManager.load("entities/explosion_enemy/1.png", Texture.class);
        assetManager.load("entities/explosion_enemy/2.png", Texture.class);
        assetManager.load("entities/explosion_enemy/3.png", Texture.class);
        assetManager.load("entities/explosion_enemy/4.png", Texture.class);
        assetManager.load("entities/explosion_enemy/5.png", Texture.class);
        assetManager.load("entities/explosion_enemy/6.png", Texture.class);

        batch = new SpriteBatch();

        //cargar todos los recursos
        loadingScreen = new LoadingScreen(this, assetManager);
        setScreen(loadingScreen);

    }

    public void finishLoading()
    {
        //cargar todas las pantallas

        level1 = new Level1(this);
        gameOverScreen = new GameOverScreen(this);
        winScreen = new WinScreen(this);
        helpScreen = new HelpScreen(this);
        aboutScreen = new AboutScreen(this);
        menuScreen = new MenuScreen(this);
        rankingScreen = new RankingScreen(Ranking.getInstance(), this);

        setScreen(getMenuScreen());
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

    public static AboutScreen getAboutScreen() {
        return aboutScreen;
    }

    public static GameOverScreen getGameOverScreen() {
        return gameOverScreen;
    }

    public static HelpScreen getHelpScreen() {
        return helpScreen;
    }

    public static MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public static WinScreen getWinScreen() {
        return winScreen;
    }

    public static Level1 getLevel1() {
        return level1;
    }

    public static RankingScreen getRankingScreen() {return rankingScreen;}

}
