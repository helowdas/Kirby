package com.marbro.entities.enemies.waddle_dee;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import com.marbro.entities.player.Kirby;
import com.marbro.entities.player.kirby_base.Kirby_base;

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
            actor.recibirDamage(-2);
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
            actor.recibirDamage(-2);
        }

        if(hanColisionado(contact, actor, "attack"))
        {
            if (kirby.getAttack())
            {
                actor.setColPlayer(true);
                actor.recibirDamage(-2);
            }
        }

        if(hanColisionado(contact, actor, kirby))
        {
            actor.setColPlayer(true);
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
            actor.setOnGround(true);
        }

        if (hanColisionado(contact, actor, "spike"))
        {
            actor.setOnSpike(true);
        }

        if (hanColisionado(contact, actor, "wall"))
        {
            actor.setOnWall(true);
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

    private boolean hanColisionado(Contact contact, Object userA, Object userB)
    {
        return (contact.getFixtureA().getUserData().equals(userA) && contact.getFixtureB().getUserData().equals(userB))
            || (contact.getFixtureA().getUserData().equals(userB) && contact.getFixtureB().getUserData().equals(userA));
    }

    public void actReferencia(Kirby kirby){
        this.kirby = kirby;
    }

}


