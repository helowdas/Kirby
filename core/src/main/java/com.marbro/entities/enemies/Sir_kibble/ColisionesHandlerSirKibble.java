package com.marbro.entities.enemies.Sir_kibble;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee;

public class ColisionesHandlerSirKibble implements ContactListener {
    private sir_kibble actor;

    public ColisionesHandlerSirKibble(sir_kibble actor) {
        this.actor = actor;
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

        if (hanColisionado(contact, actor, "player")) {
            actor.setColPlayer(true);
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

        if (hanColisionado(contact, actor, "player")) {
            actor.setColPlayer(true);
        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    private boolean hanColisionado(Contact contact, Object userA, Object userB)
    {
        return (contact.getFixtureA().getUserData().equals(userA) && contact.getFixtureB().getUserData().equals(userB))
            || (contact.getFixtureA().getUserData().equals(userB) && contact.getFixtureB().getUserData().equals(userA));
    }
}
