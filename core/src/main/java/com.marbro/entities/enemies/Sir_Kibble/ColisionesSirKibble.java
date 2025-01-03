package com.marbro.entities.enemies.Sir_Kibble;

import com.badlogic.gdx.physics.box2d.*;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee;
import com.marbro.entities.player.Kirby;


public class ColisionesSirKibble implements ContactListener {

    private Sir_Kibble actor;
    private Kirby kirby;

    public ColisionesSirKibble(Sir_Kibble actor, Kirby kirby)
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
            kirby.quitarSalud(1);
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
