package com.marbro.entities.Palanca;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.marbro.MainGame;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.entities.Block_Mecanismo.Block_M;
import com.marbro.entities.IObserver.IObservable;
import com.marbro.entities.IObserver.IObservador;
import com.marbro.entities.enemies.Factory.Entity;
import com.marbro.entities.player.Kirby;

import java.util.ArrayList;
import java.util.List;

import static com.marbro.constants.Constantes.PPM;

public class Palanca extends Actor implements Entity, IObservable
{
    //atributos
    protected World world;
    protected Body body;
    protected float width;
    protected float height;
    protected Sprite sprite;
    protected Texture texture;

    //booleans
    private boolean flip = false;
    private boolean isActive = false;

    //suscriptores
    private List<IObservador> observadores;

    //Controlador
    private Controlador_Colisiones controlador;

    //entidades
    private ArrayList<Entity> entidades;

    public Palanca(World world, Body body, float width, float height,
                   Controlador_Colisiones controlador, Kirby kirby,  ArrayList<Entity> entidades)
    {
        this.world = world;
        this.body = body;
        body.getFixtureList().get(0).setUserData(this);
        this.width = width;
        this.height = height;
        this.texture = MainGame.getAssetManager().get("entities/lever/0.png");
        this.sprite = new Sprite(texture);


        this.controlador = controlador;
        createContactListener(kirby);

        //crear lista observadores
        observadores = new ArrayList<>();

        //suscirbir bloques
        for (Entity entidad: entidades)
        {
            if (entidad instanceof Block_M)
            {
                this.suscribir((IObservador) entidad);
            }
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

    private void createContactListener(Kirby kirby){
        CollisionHandlerPalanca colisionesHandler = new CollisionHandlerPalanca(this, kirby);
        controlador.addListener(colisionesHandler);
    }

    @Override
    public void suscribir(IObservador observador)
    {
        // TODO Auto-generated method stub
        if (!observadores.contains(observador))
        {
            observadores.add(observador);
        }
    }

    @Override
    public void desuscirbir(IObservador observador)
    {
        observadores.remove(observador);
    }

    @Override
    public void notificar()
    {
        for(IObservador observador: observadores)
        {
            observador.update(this);
        }
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

    public void active() {
        isActive = true;
        flip = true;
        sprite.flip(true, false);
        this.notificar();
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean getIsActive()
    {
        return isActive;
    }
}
