package com.marbro.entities.Platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.marbro.MainGame;
import com.marbro.entities.enemies.Factory.Entity;

import static com.marbro.constants.Constantes.*;

public class PlatformVertical extends Actor implements Entity
{
    //atributos
    private World world;
    private Body body;
    private float width;
    private float height;
    private Sprite sprite;
    private Texture texture;

    //atributos para actualizar movimiento
    private Runnable movimientoArriba;
    private Runnable movimientoAbajo;
    private boolean invertir;

    public PlatformVertical(World world, Body body, float width, float height, boolean invertir)
    {
        this.world = world;
        this.body = body;
        body.getFixtureList().get(0).setUserData("platform");
        this.width = width;
        this.height = height;
        this.texture = MainGame.getAssetManager().get("entities/platform/platformWood.png");
        this.sprite = new Sprite(texture);
        this.invertir = invertir;

        //delay inicial
        float delayInicial = (float) Math.random()/2;
        defMovimiento(body);
        if(!invertir)
        {
            this.addAction(
                Actions.forever(
                    Actions.sequence(Actions.delay(delayInicial),Actions.run(movimientoAbajo), Actions.delay(DELAY),
                        Actions.run(movimientoArriba), Actions.delay(DELAY), Actions.run(movimientoArriba),
                        Actions.delay(DELAY), Actions.run(movimientoAbajo), Actions.delay(DELAY))
                )
            );
        }
        else
        {
            this.addAction(
                Actions.forever(
                    Actions.sequence(Actions.delay(delayInicial),Actions.run(movimientoArriba), Actions.delay(DELAY),
                        Actions.run(movimientoAbajo), Actions.delay(DELAY), Actions.run(movimientoAbajo),
                        Actions.delay(DELAY), Actions.run(movimientoArriba), Actions.delay(DELAY))
                )
            );
        }


    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        // Ajustar la posicion del sprite
        float posX = (body.getPosition().x * 1) - (getWidth() / 2);
        float posY = (body.getPosition().y * 1) - (getHeight() / 2);

        setPosition(posX, posY);
        // Ajustar el tamaño del actor en píxeles, considerando PPM
        setSize(width/PPM, height/PPM); // Ajusta estos valores según el tamaño deseado de tu sprite en el mundo Box2D

        sprite.setSize(getWidth(), getHeight());
        sprite.setPosition(posX, posY);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        sprite.draw(batch);
    }

    @Override
    public void detach()
    {
        for (Fixture aux:body.getFixtureList() )
        {
            body.destroyFixture(aux);
        }

        if (body != null) {
            world.destroyBody(body);
            body = null;
        }

        this.remove(); // Asegúrate de eliminar la entidad del mundo del juego, si es necesario
    }

    public void defMovimiento(Body body)
    {
        this.movimientoArriba = new Runnable()
        {
            @Override
            public void run()
            {
                body.setLinearVelocity(0, VELOCIDAD_PLATFORM);
            }
        };

        this.movimientoAbajo = new Runnable()
        {
            @Override
            public void run()
            {
                body.setLinearVelocity(0, -VELOCIDAD_PLATFORM);
            }
        };
    }

}
