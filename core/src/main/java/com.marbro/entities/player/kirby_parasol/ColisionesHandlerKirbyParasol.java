package com.marbro.entities.player.kirby_parasol;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class ColisionesHandlerKirbyParasol implements ContactListener
{
    //atributos
    private Kirby_Parasol actor;

    //constructor
    public ColisionesHandlerKirbyParasol(Kirby_Parasol actor) {
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

        if (hanColisionado(contact, actor, "platform"))
        {
            actor.setOnPlatform(true);
            actor.setOnGround(true);
        }
        //Aqui se pueden agregar más CATEGORY_TIPOCOLISION HACERLO EN EL END CONTACT TAMBIEN
    }


    @Override
    public void endContact(Contact contact)
    {
        if (hanColisionado(contact, actor, "block"))
        {
            actor.setOnGround(false);
        }

        if (hanColisionado(contact, actor, "spike")) {
            actor.setOnSpike(false);
        }

        if(hanColisionado(contact, actor, "wall"))
        {
            actor.setOnWallRight(false);
            actor.setOnWallLeft(false);
        }

        if (hanColisionado(contact, actor, "platform"))
        {
            actor.setOnPlatform(false);
            actor.setOnGround(false);
        }

        //Agregar aquí
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold)
    {
        if (hanColisionado(contact, actor, "wall"))
        {
            Vector2 normal = oldManifold.getLocalNormal();
            if(normal.x < 0)
            {
                actor.setOnWallRight(true);
            }
            else if(normal.x > 0)
            {
                actor.setOnWallLeft(true);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    private boolean hanColisionado(Contact contact, Object userA, Object userB)
    {
        Object fixtureA = contact.getFixtureA().getUserData();
        Object fixtureB = contact.getFixtureB().getUserData();

        boolean isContact = (fixtureA.equals(userA) && fixtureB.equals(userB))
            || (fixtureA.equals(userB) && fixtureB.equals(userA));

        return isContact;
    }
}




