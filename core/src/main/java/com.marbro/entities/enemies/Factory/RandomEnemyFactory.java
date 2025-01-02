package com.marbro.entities.enemies.Factory;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee;
import com.marbro.entities.enemies.Sir_Kibble.Sir_Kibble;
import com.marbro.entities.player.Kirby;
import java.util.*;

public class RandomEnemyFactory implements EnemyFactory{
    private Random random;

    public RandomEnemyFactory(){ random = new Random();}

    @Override
    public Enemy createEnemy(World world, Body body, Controlador_Colisiones controlador, float width, float height, Kirby kirby){
        int randomInt = random.nextInt(2);
        if(randomInt == 0) return new Waddle_dee(world, body, controlador, width, height, kirby);
        return new Sir_Kibble(world, body, controlador, width, height, kirby);
    }
}
