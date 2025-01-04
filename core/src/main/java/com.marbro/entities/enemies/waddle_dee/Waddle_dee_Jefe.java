package com.marbro.entities.enemies.waddle_dee;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.marbro.colisions.Controlador_Colisiones;
import com.marbro.entities.enemies.Factory.Boss;
import com.marbro.entities.player.Kirby;

public class Waddle_dee_Jefe extends Waddle_dee implements Boss
{
    public Waddle_dee_Jefe(World world, Body body, Controlador_Colisiones controlador,
                           float width, float height, Kirby kirby)
    {
        super(world, body, controlador, width, height, kirby);
        this.salud = 20;
;    }


}
