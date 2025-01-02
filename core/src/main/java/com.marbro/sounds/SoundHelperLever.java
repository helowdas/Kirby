package com.marbro.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

public class SoundHelperLever {
    private Sound leverOnSound;
    private long lastPlayTime;
    private long cooldown;

    public SoundHelperLever() {
        leverOnSound = Gdx.audio.newSound(Gdx.files.internal("entities/lever/sounds/lever_on.mp3"));
        cooldown = 200L; // 200 ms de enfriamiento
        lastPlayTime = 0L;
    }

    public void playSound() {
        long currentTime = TimeUtils.millis();
        if (currentTime - lastPlayTime >= cooldown) {
            leverOnSound.play(0.2f);
            lastPlayTime = currentTime;
        }
    }

    public void dispose() {
        leverOnSound.dispose();
    }
}
