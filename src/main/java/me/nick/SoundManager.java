package me.nick;

import net.runelite.client.Notifier;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundManager {
    private Clip clip;

    public synchronized void playCustomSound(File directory, String sound_name) {
        File sound_file = new File(directory, sound_name);
        System.out.println("Sound file: " + sound_file);
        System.out.println("Sound name: " + sound_name);
        try {
            if (clip != null) {
                clip.close();
            }

            clip = AudioSystem.getClip();

            if (!tryLoadSound(sound_name, sound_file)) {
                return;
            }

            clip.loop(0); // Play "loop" clip once
        } catch (LineUnavailableException e) {
            CozySoundsPlugin.log("Unable to play custom sound " + sound_name);
        }
    }

    private boolean tryLoadSound(String sound_name, File sound_file) {
        if (sound_file.exists()) {
            try (InputStream fileStream = new BufferedInputStream(new FileInputStream(sound_file));
                 AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream)) {
                clip.open(sound);
                return true;
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }

        // Otherwise, load from the classpath
        try (InputStream fileStream = new BufferedInputStream(Notifier.class.getResourceAsStream(sound_name));
             AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream)) {
            clip.open(sound);
            return true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        return false;
    }
}
