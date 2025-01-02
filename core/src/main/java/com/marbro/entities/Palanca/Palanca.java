package com.marbro.entities.Palanca;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.marbro.entities.IObserver.IObservable;
import com.marbro.entities.IObserver.IObservador;
import com.marbro.entities.enemies.Factory.Entity;

import java.util.List;

public class Palanca extends Actor implements Entity, IObservable
{
    private List<IObservador> observadores;


    @Override
    public void suscribir(IObservador observador)
    {
        // TODO Auto-generated method stub
        if (!observadores.contains(observador))
        {
            observadores.add(observador);
        }
    }

    @Override
    public void desuscirbir(IObservador observador)
    {
        observadores.remove(observador);
    }

    @Override
    public void notificar()
    {
        for(IObservador observador: observadores)
        {
            observador.update(this);
        }
    }

    @Override
    public void detach() {
        
    }
}
