package com.marbro.entities.enemies;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee;

public class EnemyFactory {
    public static Actor createEnemy(String tipo, World world, Stage stage, float x, float y, Controlador_Colisiones controlador) {
        switch(tipo){
            case "Waddle_dee":
                return new Waddle_dee(world, stage, x, y, controlador);
            default:
                throw new IllegalArgumentException("No se conoce esa entidad");
        }
    }
}
