package com.marbro.entities.player.kirby_parasol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.marbro.animation.Animation_Base_Loop;
import com.marbro.animation.Animation_Base_Normal;

public class AnimationHelperKirbyParasol {
    private Animation_Base_Loop walk;
    private Animation_Base_Loop jumping;
    private Animation_Base_Loop fall;
    private Animation_Base_Loop plane;
    private Animation_Base_Loop stand;
    private Animation_Base_Loop attack;
    private Animation_Base_Loop fly1;
    private Animation_Base_Loop fly2;

    public AnimationHelperKirbyParasol() {
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
        loadTexture(walk, "entities/player/kirby_parasol/kirby_parasol_walk/kirby_parasol_walk_", 1, 10);
        this.walk = new Animation_Base_Loop(walk, 0.06f);

        Array<TextureRegion> stand = new Array<>();
        loadTexture(stand, "entities/player/kirby_parasol/kirby_parasol_stand/kirby_parasol_stand_", 1, 1);
        this.stand = new Animation_Base_Loop(stand, 0.06f);

        Array<TextureRegion> fall1 = new Array<>();
        loadTexture(fall1, "entities/player/kirby_parasol/kirby_parasol_fall/kirby_parasol_fall_", 2, 3);
        this.fall = new Animation_Base_Loop(fall1, 0.08f);

        Array<TextureRegion> jump = new Array<>();
        loadTexture(jump, "entities/player/kirby_parasol/kirby_parasol_jump/kirby_parasol_jump_", 1, 1);
        this.jumping = new Animation_Base_Loop(jump, 0.5f);

        Array<TextureRegion> attack = new Array<>();
        loadTexture(attack, "entities/player/kirby_parasol/kirby_parasol_attack/kirby_parasol_attack_", 1, 19);
        this.attack = new Animation_Base_Loop(attack, 0.015f);

        Array<TextureRegion> plane = new Array<>();
        loadTexture(plane, "entities/player/kirby_parasol/kirby_parasol_plane/kirby_parasol_plane_", 1, 20);
        this.plane = new Animation_Base_Loop(plane, 0.1f);

        Array<TextureRegion> fly1 = new Array<>();
        loadTexture(fly1, "entities/player/kirby_parasol/kirby_parasol_fly/", 0,1);
        this.fly1 = new Animation_Base_Loop(fly1, 0.5f);

        Array<TextureRegion> fly2 = new Array<>();
        loadTexture(fly2, "entities/player/kirby_parasol/kirby_parasol_fly/", 2,7);
        this.fly2 = new Animation_Base_Loop(fly2, 0.08f);
    }

    public TextureRegion getFrame(EstadoKirbyParasol estado) {
        switch (estado) {
            case CAMINANDO:
                return walk.getFrame();
            case SALTANDO:
                return jumping.getFrame();
            case CAYENDO:
                return fall.getFrame();
            case ATACANDO:
                return attack.getFrame();
            case PLANEANDO:
                return plane.getFrame();
            case PREVOLAR:
                return fly1.getFrame();
            case VOLAR:
                return fly2.getFrame();
            default:
                return stand.getFrame();
        }
    }

    public void update(float delta){
        stand.update(delta);
        walk.update(delta);
        fall.update(delta);
        jumping.update(delta);
        plane.update(delta);
        attack.update(delta);
        fly1.update(delta);
        fly2.update(delta);
    }

    public void resetAnimation(EstadoKirbyParasol estado){
        switch (estado) {
            case CAMINANDO:
                walk.reset();
            case SALTANDO:
                jumping.reset();
            case CAYENDO:
                fall.reset();
            case ATACANDO:
                attack.reset();
            case PLANEANDO:
                plane.reset();
            case PREVOLAR:
                fly1.reset();
            case VOLAR:
                fly2.reset();
            default:
                stand.reset();
        }
    }
}

