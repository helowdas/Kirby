package com.marbro.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.marbro.animation.Animation_Base_Loop;
import com.marbro.animation.Animation_Base_Normal;

public class AnimationHelperKirby {
    //Animaciones del jugador
    private Animation_Base_Loop stand;
    private Animation_Base_Loop walk;
    private Animation_Base_Loop fall1;
    private Animation_Base_Loop fall2;
    private Animation_Base_Loop jumping;
    private Animation_Base_Normal abs;
    private Animation_Base_Loop fly1;
    private Animation_Base_Loop fly2;


    public AnimationHelperKirby() {
        loadAnimations();
    }

    public void loadTexture(Array<TextureRegion> animation, String path, int i, int f) {
        for (int k = i; i <= f; i++) {
            Texture texture = new Texture(Gdx.files.internal(path + i + ".png"));
            animation.add(new TextureRegion(texture));
        }
    }

    public void loadAnimations() {
        Array<TextureRegion> walk = new Array<>();
        loadTexture(walk, "entities/player/kirby_walk/kirby_walk_", 1, 10);
        this.walk = new Animation_Base_Loop(walk, 0.06f);

        Array<TextureRegion> stand = new Array<>();
        loadTexture(stand, "entities/player/kirby_stand/kirby_stand_", 1, 1);
        this.stand = new Animation_Base_Loop(stand, 0.06f);

        Array<TextureRegion> fall1 = new Array<>();
        loadTexture(fall1, "entities/player/kirby_fall/kirby_fall_", 1, 7);
        this.fall1 = new Animation_Base_Loop(fall1, 0.08f);

        Array<TextureRegion> fall2 = new Array<>();
        loadTexture(fall2, "entities/player/kirby_fall/kirby_fall_", 8, 9);
        this.fall2 = new Animation_Base_Loop(fall2, 0.2f);

        Array<TextureRegion> jump = new Array<>();
        loadTexture(jump, "entities/player/kirby_jump/kirby_jump_", 1, 1);
        this.jumping = new Animation_Base_Loop(jump, 0.5f);

        Array<TextureRegion> abs = new Array<>();
        loadTexture(abs, "entities/player/kirby_abs/kirby_abs_", 1, 7);
        this.abs = new Animation_Base_Normal(abs, 0.08f);

        Array<TextureRegion> fly1 = new Array<>();
        loadTexture(fly1, "entities/player/kirby_fly/", 0, 3);
        this.fly1 = new Animation_Base_Loop(fly1, 0.1f);

        Array<TextureRegion> fly2 = new Array<>();
        loadTexture(fly2, "entities/player/kirby_fly/", 4, 9);
        this.fly2 = new Animation_Base_Loop(fly2, 0.08f);
    }

    public TextureRegion getFrame(EstadoKirby estado) {
        TextureRegion frame;
        switch (estado) {
            case CAMINANDO:
                frame = walk.getFrame();
                break;
            case SALTANDO:
                frame = jumping.getFrame();
                break;
            case CAYENDO1:
                frame = fall1.getFrame();
                break;
            case CAYENDO2:
                frame = fall2.getFrame();
                break;
            case ASPIRANDO:
                frame = abs.getFrame();
                break;
            case VOLAR:
                frame = fly2.getFrame();
                break;
            case PREVOLAR:
                frame = fly1.getFrame();
                break;
            default:
                frame = stand.getFrame();
                break;
        }
        return frame;
    }

    public void update(float delta){
        stand.update(delta);
        walk.update(delta);
        fall1.update(delta);
        fall2.update(delta);
        jumping.update(delta);
        abs.update(delta);
        fly1.update(delta);
        fly2.update(delta);
    }
}
