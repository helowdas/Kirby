package com.marbro.entities.enemies.Factory;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee;

public interface EnemyFactory {
    public  Enemy createEnemy(World world, Stage stage, float x, float y, Controlador_Colisiones controlador);
}
