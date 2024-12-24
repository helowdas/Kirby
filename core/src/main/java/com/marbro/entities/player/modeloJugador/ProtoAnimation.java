package com.marbro.entities.player.modeloJugador;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class ProtoAnimation {
    private Animation<TextureRegion> animacion;
    private float stateTime;

    public ProtoAnimation(Array<TextureRegion> frames, float frameDuration){
        animacion = new Animation<>(frameDuration,frames, Animation.PlayMode.LOOP);
        stateTime = 0;
    }

    public void update(float delta){ stateTime+= delta; }
    public TextureRegion getFrame() { return animacion.getKeyFrame(stateTime, true);}
    public void reset(){ stateTime = 0f; }
}
