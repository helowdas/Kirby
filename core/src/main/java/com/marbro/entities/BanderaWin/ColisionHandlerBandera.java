package com.marbro.entities.BanderaWin;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.marbro.entities.Palanca.Palanca;
import com.marbro.entities.player.Kirby;

public class ColisionHandlerBandera implements ContactListener
{
    private Bandera actor;
    private Kirby kirby;

    public ColisionHandlerBandera(Bandera bandera, Kirby kirby)
    {
        this.actor = bandera;
        this.kirby = kirby;
    }

    @Override
    public void beginContact(Contact contact)
    {
        if (hanColisionado(contact, actor, kirby) && !actor.isActive())
        {
            actor.active();
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private boolean hanColisionado(Contact contact, Object userA, Object userB) {

        if (contact.getFixtureA().getUserData() != null && contact.getFixtureB().getUserData() != null)
            return (contact.getFixtureA().getUserData().equals(userA) && contact.getFixtureB().getUserData().equals(userB))
                || (contact.getFixtureA().getUserData().equals(userB) && contact.getFixtureB().getUserData().equals(userA));
        return false;
    }

    public void actReferencia(Kirby kirby)
    {
        this.kirby = kirby;
    }
}
