package com.marbro.entities.player;

import com.badlogic.gdx.physics.box2d.Body;

public interface Kirby {
    public void detach();

    public float getX();

    public float getY();
    public float getWidth();
    public float getHeight();
    public boolean isAlive();
    public void setCol(boolean col);
    public void accion(float delta);
    public Body getBody();
}
