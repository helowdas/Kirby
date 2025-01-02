package com.marbro.entities.Platforms;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.marbro.MainGame;

import static com.marbro.constants.Constantes.DELAY;
import static com.marbro.constants.Constantes.VELOCIDAD_PLATFORM;

public class PlatformHorizontal extends PlatformVertical
{
    public PlatformHorizontal(World world, Body body, float width, float height, boolean invertir, float delay)
    {
        this.world = world;
        this.body = body;
        body.getFixtureList().get(0).setUserData("platform");
        this.width = width;
        this.height = height;
        this.texture = MainGame.getAssetManager().get("entities/platform/platformWood.png");
        this.sprite = new Sprite(texture);
        this.invertir = invertir;
        this.delay = delay;

        //delay inicial
        defMovimiento(body);

        defAction(invertir);
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void defMovimiento(Body body)
    {
        this.movimientoArriba = new Runnable()
        {
            @Override
            public void run()
            {
                body.setLinearVelocity(VELOCIDAD_PLATFORM, 0);
            }
        };

        this.movimientoAbajo = new Runnable()
        {
            @Override
            public void run()
            {
                body.setLinearVelocity(-VELOCIDAD_PLATFORM, 0);
            }
        };
    }

    @Override
    public void defAction(boolean invertir)
    {
        super.defAction(invertir);
    }
}
