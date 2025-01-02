package com.marbro.entities.enemies.waddle_dee;

import com.badlogic.gdx.physics.box2d.*;
import com.marbro.entities.player.Kirby;

import java.util.HashMap;
import java.util.HashSet;

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
        }

        if (hanColisionado(contact, actor, "wall"))
        {
            actor.setOnWall(true);
        }

        if (hanColisionado(contact, actor, "platform"))
        {
            actor.setOnGround(true);
        }

        if(hanColisionado(contact, actor, kirby))
        {
            actor.setColPlayer(true);
            kirby.setCol(true);
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

            if(actor.getTimeCollision())
            {
                contact.setEnabled(true);
            }
            else
            {
                contact.setEnabled(false);
            }
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

}


