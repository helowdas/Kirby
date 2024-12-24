package com.marbro.entities.player.modeloJugador;
import com.badlogic.gdx.physics.box2d.*;

public class MyContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {
    private boolean isGrounded;

    @Override
    public void beginContact(Contact contact) {
        // Obtener los objetos en colisión
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Verificar si una de las colisiones es el suelo
        if (fixtureA.getUserData() != null && fixtureA.getUserData().equals("block")) {
            isGrounded = true;
        } else if (fixtureB.getUserData() != null && fixtureB.getUserData().equals("block")) {
            isGrounded = true;
        }
    }

    @Override
    public void endContact(Contact contact) {
        // Obtener los objetos en colisión
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Verificar si una de las colisiones es el suelo
        if (fixtureA.getUserData() != null && fixtureA.getUserData().equals("block")) {
            isGrounded = false;
        } else if (fixtureB.getUserData() != null && fixtureB.getUserData().equals("block")) {
            isGrounded = false;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // No es necesario para este ejemplo
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // No es necesario para este ejemplo
    }

    public boolean isGrounded() {
        return isGrounded;
    }
}

