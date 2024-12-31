package com.marbro.entities.enemies.Factory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;

public interface Enemy {
    public void updateEnemyState();
    public TextureRegion drawEnemy();
    Body getBody();
    void removeEnemy();
    void attack();
}
