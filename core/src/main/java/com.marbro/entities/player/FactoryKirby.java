package com.marbro.entities.player;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.marbro.entities.enemies.Factory.Enemy;
import com.marbro.entities.enemies.Sir_Kibble.Sir_Kibble;
import com.marbro.entities.enemies.waddle_dee.Waddle_dee;
import com.marbro.entities.player.kirby_base.ColisionesHandlerKirby;
import com.marbro.entities.player.kirby_base.Kirby_base;
import com.marbro.entities.player.kirby_parasol.Kirby_Parasol;
import com.marbro.screens.level1.Level1;

import static com.marbro.constants.Constantes.PPM;

public class FactoryKirby {
    public Kirby createKirby(Level1 gameScreen, Body body, Rectangle rectangle, Enemy type_enemy, int salud, ColisionesHandlerKirby colisionesHandlerKirby) {

        if(type_enemy == null)
        {
            return new Kirby_base(gameScreen.getWorld(),
                gameScreen.getStage(), body, gameScreen.getControlador(),
                rectangle.width / PPM, rectangle.height / PPM, gameScreen, salud, colisionesHandlerKirby);
        }

        if(type_enemy instanceof Waddle_dee)
        {
            return new Kirby_Parasol(gameScreen.getWorld(), gameScreen.getStage(), body,
                gameScreen.getControlador(), rectangle.width / PPM, rectangle.height / PPM,
                gameScreen, salud, colisionesHandlerKirby);
        }
        else if (type_enemy instanceof Sir_Kibble)
        {
            return new Kirby_Parasol(gameScreen.getWorld(), gameScreen.getStage(), body,
                gameScreen.getControlador(), rectangle.width / PPM, rectangle.height / PPM,
                gameScreen, salud, colisionesHandlerKirby);
        }
        else
        {
            return new Kirby_base(gameScreen.getWorld(),
                gameScreen.getStage(), body, gameScreen.getControlador(),
                rectangle.width / PPM, rectangle.height / PPM, gameScreen, salud, colisionesHandlerKirby);

        }
    }
}
