package com.marbro.entities.player;

import com.badlogic.gdx.physics.box2d.*;

import static com.marbro.constants.Constantes.*;

public class ColisionesHandlerKirby implements ContactListener {
    private Kirby actor;

    public ColisionesHandlerKirby(Kirby actor) {
        this.actor = actor;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();

        if (userDataA instanceof Kirby || userDataB instanceof Kirby) {
            Kirby actor = userDataA instanceof Kirby ? (Kirby) userDataA : (Kirby) userDataB;

            if (fixtureA.getFilterData().categoryBits == CATEGORY_BLOCK ||
                fixtureB.getFilterData().categoryBits == CATEGORY_BLOCK) {
                actor.setOnGround(true);
            }

            if (fixtureA.getFilterData().categoryBits == CATEGORY_SPIKE||
                fixtureB.getFilterData().categoryBits == CATEGORY_SPIKE) {
                actor.setOnSpike(true);
            }

            if (fixtureA.getFilterData().categoryBits == CATEGORY_WALL||
                fixtureB.getFilterData().categoryBits == CATEGORY_WALL) {
                actor.setOnWall(true);
            }
            //Aqui se pueden agregar más CATEGORY_TIPOCOLISION HACERLO EN EL END CONTACT TAMBIEN
        }
    }


    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();

        if (userDataA instanceof Kirby || userDataB instanceof Kirby) {
            Kirby actor = userDataA instanceof Kirby ? (Kirby) userDataA : (Kirby) userDataB;

            if (fixtureA.getFilterData().categoryBits == CATEGORY_BLOCK ||
                fixtureB.getFilterData().categoryBits == CATEGORY_BLOCK) {
                actor.setOnGround(false);
            }

            if (fixtureA.getFilterData().categoryBits == CATEGORY_SPIKE||
                fixtureB.getFilterData().categoryBits == CATEGORY_SPIKE) {
                actor.setOnSpike(false);
            }

            if (fixtureA.getFilterData().categoryBits == CATEGORY_WALL||
                fixtureB.getFilterData().categoryBits == CATEGORY_WALL) {
                actor.setOnWall(false);
            }

            //Agregar aquí
        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

}



