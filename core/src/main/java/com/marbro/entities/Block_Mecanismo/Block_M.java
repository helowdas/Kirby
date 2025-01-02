package com.marbro.entities.Block_Mecanismo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.marbro.MainGame;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.entities.IObserver.IObservable;
import com.marbro.entities.IObserver.IObservador;
import com.marbro.entities.Palanca.CollisionHandlerPalanca;
import com.marbro.entities.enemies.Factory.Entity;
import com.marbro.entities.player.Kirby;

import java.util.ArrayList;

import static com.marbro.constants.Constantes.PPM;

public class Block_M extends Actor implements Entity, IObservador
{
    //atributos
    protected World world;
    protected Body body;
    protected float width;
    protected float height;
    protected Sprite sprite;
    protected Texture texture;
    private Controlador_Colisiones controlador;
    private Fixture fixture;
    public boolean isDestroyed = false;

    public Block_M(World world, Body body, float width, float height)
    {
        this.world = world;
        this.body = body;
        fixture = body.getFixtureList().get(0);
        fixture.setUserData("wall");
        this.width = width;
        this.height = height;
        this.texture = MainGame.getAssetManager().get("entities/block_Mecanismo/0.png");
        this.sprite = new Sprite(texture);

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
    public void update(IObservable observable)
    {
        this.remove();
        fixture.setSensor(true);
        isDestroyed = true;
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

        this.remove();
        // Asegúrate de eliminar la entidad del mundo del juego, si es necesario
    }

    @Override
    public Body getBody() {
        return body;
    }
}
