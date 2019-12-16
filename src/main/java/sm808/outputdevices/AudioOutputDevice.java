package sm808.outputdevices;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import sm808.models.Event;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class AudioOutputDevice implements OutputDevice {
    @Override
    public void play(Set<Event> events) throws PlaybackException {
        for (Event event : events) {
            playEvent(event);
        }
    }

    private void playEvent(Event event) throws PlaybackException {
        URL audioUrl = getAudioUrl(event);
        try {
            // This makes unit tests tricky - a better approach might be to supply a ClipProvider to this class
            Clip clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioUrl);
            clip.open(ais);
            clip.loop(0);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            throw new PlaybackException(e);
        }
    }

    @VisibleForTesting
    protected URL getAudioUrl(Event eventType) {
        Map<Event, String> eventToFileName = ImmutableMap.<Event, String>builder()
                .put(Event.KICK, "audio/kick.wav")
                .put(Event.SNARE, "audio/snare.wav")
                .put(Event.HIHAT, "audio/hihat.wav")
                .build();

        // Arbitrary choice - fill in unknowns with kick sounds. Could throw an IllegalArgumentException instead.
        String path = eventToFileName.getOrDefault(eventType, "audio/kick.wav");
        return getClass().getClassLoader().getResource(path);
    }
}
