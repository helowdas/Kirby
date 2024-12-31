package com.marbro.entities.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.marbro.constants.Constantes.*;

public class ColisionesHandlerKirby implements ContactListener
{
    //atributos
    private Kirby actor;

    //constructor
    public ColisionesHandlerKirby(Kirby actor) {
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
                System.out.println("Derecha");
            }
            else if(normal.x > 0)
            {
                actor.setOnWallLeft(true);
                System.out.println("izquierda");
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




