package com.marbro.entities.player;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.marbro.entities.player.kirby_base.Kirby_base;
import com.marbro.entities.player.kirby_parasol.Kirby_Parasol;
import com.marbro.screens.level1.Level1;

import static com.marbro.constants.Constantes.PPM;

public class FactoryKirby {
    public Kirby createKirby(Level1 gameScreen, Body body, Rectangle rectangle, String type_enemy, int salud) {
        switch (type_enemy) {
            case "Waddle_dee":
                return new Kirby_Parasol(gameScreen.getWorld(), gameScreen.getStage(), body,
                    gameScreen.getControlador(), rectangle.width / PPM, rectangle.height / PPM,
                    gameScreen, salud);
            default:
                return new Kirby_base(gameScreen.getWorld(),
                    gameScreen.getStage(), body, gameScreen.getControlador(),
                    rectangle.width / PPM, rectangle.height / PPM, gameScreen, salud);

        }
    }
}
