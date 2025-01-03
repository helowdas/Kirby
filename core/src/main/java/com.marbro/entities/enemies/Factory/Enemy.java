package com.marbro.entities.enemies.Factory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.marbro.entities.player.Kirby;

public interface Enemy extends Entity {
    public void updateEnemyState();
    public TextureRegion drawEnemy();
    Body getBody();
    void removeEnemy();
    String getUdata();
    void setHerido(boolean hurt);
    void actReferencia(Kirby kirby);
}
