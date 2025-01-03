package com.marbro.entities.BanderaWin;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.marbro.entities.player.Kirby;

public class OpenAnimation
{
    private Animation<Sprite> animation;
    private Kirby player;
    private AssetManager manager;
    private Sprite frameActual;
    private Sprite[] frames;
    private Texture[] texturas;
    private float tiempo = 0;

    public OpenAnimation(AssetManager manager)
    {
        this.manager = manager;

        texturas = new Texture[8];
        texturas[0] = manager.get("entities/BanderaWin/0.png");
        texturas[1] = manager.get("entities/BanderaWin/1.png");
        texturas[2] = manager.get("entities/BanderaWin/2.png");
        texturas[3] = manager.get("entities/BanderaWin/3.png");
        texturas[4] = manager.get("entities/BanderaWin/4.png");
        texturas[5] = manager.get("entities/BanderaWin/5.png");
        texturas[6] = manager.get("entities/BanderaWin/6.png");
        texturas[7] = manager.get("entities/BanderaWin/7.png");

        frames = new Sprite[8];
        for(int i = 0; i < 8; i++)
        {
            frames[i] = new Sprite(texturas[i]);
        }

        animation = new Animation(1/9f, frames);

    }

    public Sprite getFrameActual(float deltaTime)
    {
        tiempo += deltaTime;
        frameActual = animation.getKeyFrame(tiempo, true);
        return frameActual;
    }
}
