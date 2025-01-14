package com.marbro.entities.BanderaWin;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.marbro.MainGame;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.entities.Block_Mecanismo.Block_M;
import com.marbro.entities.Palanca.CollisionHandlerPalanca;
import com.marbro.entities.enemies.Factory.Entity;
import com.marbro.entities.player.Kirby;

import java.util.ArrayList;

import static com.marbro.constants.Constantes.PPM;

public class Bandera extends Actor implements Entity
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

    //Controlador
    private Controlador_Colisiones controlador;
    private ColisionHandlerBandera colisiones;

    //animacion
    private OpenAnimation animation;
    private Runnable winRunnable;

    //kirby
    Kirby kirby;

    public Bandera(World world, Body body, float width, float height,
                   Controlador_Colisiones controlador, Kirby kirby)
    {
        this.world = world;
        this.body = body;
        body.getFixtureList().get(0).setUserData(this);
        this.width = width;
        this.height = height;
        this.texture = MainGame.getAssetManager().get("entities/BanderaWin/0.png");
        this.sprite = new Sprite(texture);

        this.controlador = controlador;
        createContactListener(kirby);

        //animacion
        animation = new OpenAnimation(MainGame.getAssetManager());
        this.kirby = kirby;

        //definir runnable
        defWinRun(this, controlador);

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

        if(isActive)
        {
            sprite = animation.getFrameActual(delta);
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        sprite.draw(batch);
    }

    private void createContactListener(Kirby kirby){
        colisiones = new ColisionHandlerBandera(this, kirby);
        controlador.addListener(colisiones);
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

    public void active()
    {
        if(kirby.isBossDefeat())
        {
            isActive = true;
            float timaAnimation = 0.85f;

            this.addAction(Actions.sequence(
                Actions.delay(timaAnimation), Actions.run(winRunnable)
            ));
        }

    }

    @Override
    public Body getBody() {
        return body;
    }

    public boolean isActive() {
        return isActive;
    }

    public void defWinRun(Bandera bandera, Controlador_Colisiones controlador)
    {
        winRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                bandera.kirby.setWin(true);
            }
        };
    }

    public void actReferencia(Kirby kirby)
    {
        this.kirby = kirby;
        colisiones.actReferencia(kirby);
    }
}
