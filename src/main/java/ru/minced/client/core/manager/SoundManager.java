package ru.minced.client.core.manager;

import lombok.Getter;
import ru.minced.client.util.IMinecraft;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundManager implements IMinecraft {
    private final Map<String, String> soundMap = new HashMap<>();

    @Getter
    private float volume = 1.0f;

    public void init() {
        registerSound("enable", "module_enable.wav");
        registerSound("disable", "module_disable.wav");
        registerSound("applePay", "ApplePay.wav");
    }

    private void registerSound(String name, String fileName) {
        soundMap.put(name, fileName);
    }

    public void setVolume(float volume) {
        if (volume < 0.0f) volume = 0.0f;
        if (volume > 1.0f) volume = 1.0f;
        this.volume = volume;
    }

    public void play(String soundName) {
        play(soundName, volume);
    }

    public void play(String soundName, float volume) {
        String fileName = soundMap.get(soundName);
        if (fileName == null) {
            System.out.println("Звук с именем " + soundName + " не найден");
            return;
        }

        try {
            InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("assets/minced/sounds/" + fileName);
            if (resourceStream == null) {
                System.out.println("Файл звука не найден: " + fileName);
                return;
            }

            InputStream bufferedInputStream = new BufferedInputStream(resourceStream);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInputStream);

            AudioFormat format = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip clip = (Clip) AudioSystem.getLine(info);

            clip.open(audioStream);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (volume <= 0.0f) ? gainControl.getMinimum() :
                        (float) (20.0 * Math.log10(volume));
                dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                gainControl.setValue(dB);
            }

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audioStream.close();
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}