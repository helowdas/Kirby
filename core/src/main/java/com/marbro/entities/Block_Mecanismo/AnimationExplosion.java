package com.marbro.entities.Block_Mecanismo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.marbro.entities.player.Kirby;

public class AnimationExplosion
{
    private Animation<Sprite> animation;
    private Kirby player;
    private AssetManager manager;
    private Sprite frameActual;
    private Sprite[] frames;
    private Texture[] texturas;
    private float tiempo = 0;

    public AnimationExplosion(AssetManager manager)
    {
        this.manager = manager;

        texturas = new Texture[6];
        texturas[0] = manager.get("entities/block_Mecanismo/1-(2).png");
        texturas[1] = manager.get("entities/block_Mecanismo/1.png");
        texturas[2] = manager.get("entities/block_Mecanismo/2.png");
        texturas[3] = manager.get("entities/block_Mecanismo/3.png");
        texturas[4] = manager.get("entities/block_Mecanismo/4.png");
        texturas[5] = manager.get("entities/block_Mecanismo/5.png");

        frames = new Sprite[6];
        for(int i = 0; i < 6; i++)
        {
            frames[i] = new Sprite(texturas[i]);
        }

        animation = new Animation(1/8f, frames);

    }

    public Sprite getFrameActual(float deltaTime)
    {
        tiempo += deltaTime;
        frameActual = animation.getKeyFrame(tiempo, true);
        return frameActual;
    }
}
