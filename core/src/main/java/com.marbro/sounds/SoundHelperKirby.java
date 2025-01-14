package com.marbro.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.HashMap;

public class SoundHelperKirby {
    private HashMap<String, Sound> sounds;
    private HashMap<String, Long> lastPlayTime;
    private HashMap<String, Long> cooldowns;
    private HashMap<String, Long> soundIds; // Para guardar los IDs de reproducción

    public SoundHelperKirby() {
        sounds = new HashMap<>();
        lastPlayTime = new HashMap<>();
        cooldowns = new HashMap<>();
        soundIds = new HashMap<>(); // Inicializar el HashMap de IDs

        // Cargar los sonidos
        sounds.put("jump", Gdx.audio.newSound(Gdx.files.internal("entities/player/sounds/kirby_jump.mp3")));
        sounds.put("hurt", Gdx.audio.newSound(Gdx.files.internal("entities/player/sounds/kirby_hurt.mp3")));
        sounds.put("abs", Gdx.audio.newSound(Gdx.files.internal("entities/player/sounds/kirby_abs.mp3")));
        sounds.put("attack", Gdx.audio.newSound(Gdx.files.internal("entities/player/sounds/parasol_sound.mp3")));

        // Establecer tiempos de enfriamiento en milisegundos
        cooldowns.put("jump", 200L); // 200 ms
        cooldowns.put("hurt", 300L); // 300 ms
        cooldowns.put("abs", 2000L); // 2000 ms (2 segundos)
        cooldowns.put("attack", 100L);

        // Inicializar tiempos de última reproducción
        lastPlayTime.put("jump", 0L);
        lastPlayTime.put("hurt", 0L);
        lastPlayTime.put("abs", 0L);
        lastPlayTime.put("attack",0L);
    }

    public long playSound(String sound) {
        long currentTime = TimeUtils.millis();
        Long lastTime = lastPlayTime.get(sound);
        Long cooldown = cooldowns.get(sound);

        if (lastTime != null && cooldown != null) {
            if (currentTime - lastTime >= cooldown) {
                Sound s = sounds.get(sound);
                if (s != null) {
                    long soundId = -1;
                    if (sound.equals("jump")) {
                        soundId = s.play(0.1f);
                    } else if (sound.equals("hurt")) {
                        soundId = s.play(0.5f);
                    } else if (sound.equals("abs")) {
                        soundId = s.loop(1.0f); // Reproduce en loop para detenerlo fácilmente
                    } else if (sound.equals("attack")){
                        soundId = s.play(0.25f);
                    }
                    soundIds.put(sound, soundId);
                    lastPlayTime.put(sound, currentTime);
                    return soundId;
                }
            }
        }
        return -1; // Retorna -1 si no se pudo reproducir el sonido
    }

    public void stopSound(String sound) {
        Sound s = sounds.get(sound);
        Long soundId = soundIds.get(sound);
        if (s != null && soundId != null) {
            s.stop(soundId);
        }
    }

    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
    }
}
