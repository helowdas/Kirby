package com.marbro.entities.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.marbro.entities.enemies.Factory.Enemy;

public interface Kirby {
    public void detach();

    public float getX();

    public float getY();
    public float getWidth();
    public float getHeight();
    public int getSalud();
    public void quitarSalud(int damage_points);
    public void setCol(boolean col);
    public void accion(float delta);
    public Body getBody();
    public boolean isWin();
    public void setWin(boolean win);
    public boolean isBossDefeat();
    public void setBossDefeat(boolean boss_defeat);
    public boolean getAttack();
    public void setPower(Enemy Udata);
    public void setOnWallRight(boolean colision);
    public void setOnWallLeft(boolean onWallLeft);
    public void setOnPlatform(boolean onPlatform);
    public void setOnGround(boolean colision);
    public void setOnSpike(boolean colision);
}
