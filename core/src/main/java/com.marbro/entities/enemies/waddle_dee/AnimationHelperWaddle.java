package com.marbro.entities.enemies.waddle_dee;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.marbro.animation.Animation_Base_Loop;
import com.marbro.entities.enemies.Sir_Kibble.EstadoSirKibble;

public class AnimationHelperWaddle {

    private Animation_Base_Loop walk;
    private Animation_Base_Loop fall;
    private Animation_Base_Loop hurt;

    public AnimationHelperWaddle() { loadAnimations(); }

    public void loadTexture(Array<TextureRegion> animation, String path, int i, int f){
        for (int k = i; i <= f; i++){
            Texture texture = new Texture(Gdx.files.internal(path + i + ".png"));
            animation.add(new TextureRegion(texture));
        }
    }

    public void loadAnimations() {
        Array<TextureRegion> walk_waddle = new Array<>();
        loadTexture(walk_waddle, "entities/waddle_dee/waddle_dee_walk/waddle_dee_walk_", 1, 4);
        this.walk = new Animation_Base_Loop( walk_waddle,0.1f);

        Array<TextureRegion> fall_waddle = new Array<>();
        loadTexture(fall_waddle, "entities/waddle_dee/waddle_dee_fall/waddle_dee_fall_", 1, 2);
        this.fall = new Animation_Base_Loop(fall_waddle,0.1f);

        Array<TextureRegion> hurt_waddle = new Array<>();
        loadTexture(hurt_waddle, "entities/waddle_dee/waddle_dee_hurt/waddle_dee_hurt_", 1, 1);
        this.hurt = new Animation_Base_Loop(hurt_waddle,0.1f);
    }

    public TextureRegion getFrame(EstadoWaddleDee estado) {
        TextureRegion frame;
        switch (estado) {
            case CAMINANDO:
                frame = walk.getFrame();
                break;
            case CAYENDO:
                frame =  fall.getFrame();
                break;
            case HURT:
                frame = hurt.getFrame();
                break;
            default:
                frame = walk.getFrame();
                break;
        }
        return frame;
    }

    public void update (float delta){
        walk.update(delta);
        fall.update(delta);
        hurt.update(delta);
    }


}
