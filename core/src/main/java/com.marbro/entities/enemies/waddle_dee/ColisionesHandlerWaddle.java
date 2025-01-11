package com.marbro.entities.enemies.waddle_dee;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import com.marbro.entities.player.Kirby;
import com.marbro.entities.player.kirby_base.Kirby_base;
import static com.marbro.constants.Constantes.*;

;

public class ColisionesHandlerWaddle implements ContactListener
{
    private Waddle_dee actor;
    private Kirby kirby;

    public ColisionesHandlerWaddle(Waddle_dee actor, Kirby kirby)
    {
        this.actor = actor;
        this.kirby = kirby;
    }

    @Override
    public void beginContact(Contact contact)
    {
        if (hanColisionado(contact, actor, "block"))
        {
            actor.setOnGround(true);
        }

        if (hanColisionado(contact, actor, "spike"))
        {
            actor.setOnSpike(true);
            actor.recibirDamage(- actor.getSalud());
        }

        if (hanColisionado(contact, actor, "wall"))
        {
            actor.setOnWall(true);
        }

        if (hanColisionado(contact, actor, "platform"))
        {
            actor.setOnGround(true);
        }

        if(hanColisionado(contact, actor, "abismo"))
        {
            actor.recibirDamage(- actor.getSalud());
        }

        if(hanColisionado(contact, actor, "attack"))
        {
            if (kirby.getAttack())
            {
                actor.setColPlayer(true);
                actor.recibirDamage(- ATTACK_DAMAGE_KIRBY_PARASOL);
            }
        }

        if(hanColisionado(contact, actor, kirby))
        {
            actor.setColPlayer(true);
            actor.recibirDamage(-1);
            kirby.setCol(true);

            if(!Gdx.input.isKeyPressed(Input.Keys.F))
            {
                kirby.quitarSalud(1);
            }
        }
    }


    @Override
    public void endContact(Contact contact)
    {
        if (hanColisionado(contact, actor, "block"))
        {
            actor.setOnGround(false);
        }

        if (hanColisionado(contact, actor, "spike"))
        {
            actor.setOnSpike(false);
        }

        if (hanColisionado(contact, actor, "wall"))
        {
            actor.setOnWall(false);
        }

        if (hanColisionado(contact, actor, "platform"))
        {
            actor.setOnGround(false);
        }

        if(hanColisionado(contact, actor, kirby))
        {
            actor.setColPlayer(false);
            kirby.setCol(false);
        }

    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold)
    {
        if(hanColisionado(contact, actor, kirby))
        {
            contact.setEnabled(actor.getTimeCollision());
        }

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse)
    {

    }

    private boolean hanColisionado(Contact contact, Object userA, Object userB) {
        if (contact.getFixtureA().getUserData() == null && contact.getFixtureB().getUserData() == null)
            System.out.println(contact.getFixtureA().getUserData() + " " + contact.getFixtureB().getUserData());

        if (contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() != null)
            return (contact.getFixtureA().getUserData().equals(userA) && contact.getFixtureB().getUserData().equals(userB))
                || (contact.getFixtureA().getUserData().equals(userB) && contact.getFixtureB().getUserData().equals(userA));
        return false;
    }


    public void actReferencia(Kirby kirby){
        this.kirby = kirby;
    }

}


