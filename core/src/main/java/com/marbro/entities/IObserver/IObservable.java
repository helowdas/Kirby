package com.marbro.entities.IObserver;

public interface IObservable
{
    void suscribir(IObservador observador);
    void desuscirbir(IObservador observador);
    void notificar();
}
