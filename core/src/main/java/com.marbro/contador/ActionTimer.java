package com.marbro.contador;

import com.badlogic.gdx.utils.TimeUtils;

public class ActionTimer {
    private long startTime;
    private long accumulatedTime;
    private boolean running;

    public ActionTimer() {
        startTime = 0;
        accumulatedTime = 0;
        running = false;
    }

    // Iniciar o reanudar el temporizador
    public void start() {
        if (!running) {
            startTime = TimeUtils.nanoTime();
            running = true;
        }
        // Si ya está corriendo, no hace nada
    }

    // Pausar el temporizador
    public void pause() {
        if (running) {
            long currentTime = TimeUtils.nanoTime();
            accumulatedTime += (currentTime - startTime);
            running = false;
        }
    }

    // Reiniciar el temporizador deteniendo cualquier acumulación previa
    public void reset() {
        startTime = 0;
        accumulatedTime = 0;
        running = false;
    }

    // Obtener el tiempo transcurrido en segundos desde que el temporizador comenzó
    public float getElapsedTime() {
        if (running) {
            long currentTime = TimeUtils.nanoTime();
            return (accumulatedTime + (currentTime - startTime)) / 1000000000.0f;
        }
        return accumulatedTime / 1000000000.0f;
    }

    // Comprobar si el temporizador está en marcha
    public boolean isRunning() {
        return running;
    }
}
