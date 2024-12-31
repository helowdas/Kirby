package com.marbro.entities.enemies.waddle_dee;

import com.badlogic.gdx.physics.box2d.*;
import com.marbro.entities.player.Kirby;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static com.marbro.constants.Constantes.*;;

public class ColisionesHandlerWaddle implements ContactListener {
    private Waddle_dee actor;
    private Kirby kirby;
    // Atributos para controlar el tiempo de desactivación
    private HashSet<Contact> disabledContacts = new HashSet<>();
    private HashMap<Contact, Long> contactDisableTimes = new HashMap<>();
    private static final long DISABLE_DURATION = 1500; // Duración en milisegundos (3 segundos)

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

        if(hanColisionado(contact, actor, kirby))
        {
            actor.setColPlayer(false);
            kirby.setCol(false);
        }

    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold)
    {


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


