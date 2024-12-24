package com.marbro.entities.enemies.waddle_dee;

import com.badlogic.gdx.physics.box2d.*;

import static com.marbro.constants.Constantes.*;;

public class ColisionesHandlerWaddle implements ContactListener {
    private Waddle_dee actor;

    public ColisionesHandlerWaddle(Waddle_dee actor) {
        this.actor = actor;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object userDataA = fixtureA.getUserData();
        Object userDataB = fixtureB.getUserData();

        if (userDataA instanceof Waddle_dee || userDataB instanceof Waddle_dee) {
            Waddle_dee actor = userDataA instanceof Waddle_dee ? (Waddle_dee) userDataA : (Waddle_dee) userDataB;

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

            if (fixtureA.getFilterData().categoryBits == CATEGORY_PLAYER||
                fixtureB.getFilterData().categoryBits == CATEGORY_PLAYER) {
                actor.setColPlayer(true);
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

        if (userDataA instanceof Waddle_dee || userDataB instanceof Waddle_dee) {
            Waddle_dee actor = userDataA instanceof Waddle_dee ? (Waddle_dee) userDataA : (Waddle_dee) userDataB;

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

            if (fixtureA.getFilterData().categoryBits == CATEGORY_PLAYER||
                fixtureB.getFilterData().categoryBits == CATEGORY_PLAYER) {
                actor.setColPlayer(false);
            }

            //Agregar aquí
        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

}



