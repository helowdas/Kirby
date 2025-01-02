package com.marbro.entities.enemies.Factory;

import com.badlogic.gdx.physics.box2d.Body;

public interface Entity {
    void detach();
    Body getBody();
}
