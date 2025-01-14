package com.marbro.entities.player.kirby_parasol;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import static com.marbro.constants.Constantes.*;

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
            actor.setOnGround(true);
            actor.quitarSalud(1);
        }

        if (hanColisionado(contact, actor, "platform"))
        {
            actor.setOnPlatform(true);
            actor.setOnGround(true);
        }
        if (hanColisionado(contact, actor, "block"))
        {
            actor.setOnGround(true);
        }

        if (hanColisionado(contact, actor, "spike"))
        {
            actor.setOnSpike(true);
        }

        if (hanColisionado(contact, actor, "abismo"))
        {
            actor.quitarSalud(SALUD_KIRBY_MAX);
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

        if (hanColisionado(contact, actor, "spike"))
        {
            actor.setOnGround(false);
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

    private boolean hanColisionado(Contact contact, Object userA, Object userB) {

        if (contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() != null)
            return (contact.getFixtureA().getUserData().equals(userA) && contact.getFixtureB().getUserData().equals(userB))
                || (contact.getFixtureA().getUserData().equals(userB) && contact.getFixtureB().getUserData().equals(userA));
        return false;
    }
}




