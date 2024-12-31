package com.marbro.entities.enemies.Sir_Kibble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.marbro.animation.Animation_Base_Loop;

public class AnimationHelperSirKibble {

    private Animation_Base_Loop walk;
    private Animation_Base_Loop fall;
    private Animation_Base_Loop hurt;
    private Animation_Base_Loop attack;



    private boolean jump;

    public AnimationHelperSirKibble() {
        loadAnimations();
    }

    public void loadTexture(Array<TextureRegion> animation, String path, int i, int f){
        for (int k = i; i <= f; i++){
            Texture texture = new Texture(Gdx.files.internal(path + i + ".png"));
            animation.add(new TextureRegion(texture));
        }
    }

    public void loadAnimations() {
        Array<TextureRegion> walk_sirkibble = new Array<>();
        loadTexture(walk_sirkibble, "entities/Sir_Kibble/caminar/caminar_kibble_", 0, 4);
        this.walk = new Animation_Base_Loop( walk_sirkibble,0.1f);

        Array<TextureRegion> fall_sirkibble = new Array<>();
        loadTexture(fall_sirkibble, "entities/Sir_Kibble/fall/fall_", 0, 0);
        this.fall = new Animation_Base_Loop(fall_sirkibble,0.1f);

        Array<TextureRegion> hurt_sirkibble = new Array<>();
        loadTexture(hurt_sirkibble, "entities/Sir_Kibble/hurt/hurt_", 0, 0);
        this.hurt = new Animation_Base_Loop(hurt_sirkibble,0.1f);

        Array<TextureRegion> attack_sirkibble = new Array<>();
        loadTexture(attack_sirkibble, "entities/Sir_Kibble/atacar/atacar_", 0, 0);
        this.attack = new Animation_Base_Loop(attack_sirkibble,0.1f);
    }

    public TextureRegion getFrame(EstadoSirKibble estado) {
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
            case ATTACK:
                 frame = attack.getFrame();
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
        attack.update(delta);
    }

}

