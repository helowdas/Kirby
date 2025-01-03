package com.marbro.entities.AnimationExplosion;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.marbro.entities.player.Kirby;

public class AnimationExplosionEnemy
{
    private Animation<Sprite> animation;
    private Kirby player;
    private AssetManager manager;
    private Sprite frameActual;
    private Sprite[] frames;
    private Texture[] texturas;
    private float tiempo = 0;

    public AnimationExplosionEnemy(AssetManager manager)
    {
        this.manager = manager;

        texturas = new Texture[7];
        texturas[0] = manager.get("entities/explosion_enemy/0.png");
        texturas[1] = manager.get("entities/explosion_enemy/1.png");
        texturas[2] = manager.get("entities/explosion_enemy/2.png");
        texturas[3] = manager.get("entities/explosion_enemy/3.png");
        texturas[4] = manager.get("entities/explosion_enemy/4.png");
        texturas[5] = manager.get("entities/explosion_enemy/5.png");
        texturas[6] = manager.get("entities/explosion_enemy/6.png");


        frames = new Sprite[7];
        for(int i = 0; i < 7; i++)
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
