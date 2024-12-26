package com.marbro.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Animation_Base_Normal {
    private Animation<TextureRegion> animacion;
    private float stateTime;

    public Animation_Base_Normal(Array<TextureRegion> frames, float frameDuration){
        animacion = new Animation<>(frameDuration, frames, Animation.PlayMode.NORMAL);
        stateTime = 0;
    }

    public void update(float delta){
        stateTime += delta;
    }

    public TextureRegion getFrame() {
        return animacion.getKeyFrame(stateTime, false);
    }

    public void reset(){
        stateTime = 0f;
    }

    public boolean isAnimationFinished(){
        return stateTime >= animacion.getAnimationDuration();
    }

    public TextureRegion getFrameByIndex(int index){
        if (index < 0 || index >= animacion.getKeyFrames().length){
            throw new IllegalArgumentException("Index out of bounds");
        }
        return animacion.getKeyFrames()[index];
    }
}
