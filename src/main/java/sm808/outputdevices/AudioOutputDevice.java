package sm808.outputdevices;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import sm808.models.Event;

public class AudioOutputDevice implements OutputDevice {
  private final Map<Event, URL> audioUrls;

  public AudioOutputDevice() {
    audioUrls =
        ImmutableMap.<Event, URL>builder()
            .put(Event.KICK, getResource("audio/kick.wav"))
            .put(Event.SNARE, getResource("audio/snare.wav"))
            .put(Event.HIHAT, getResource("audio/hihat.wav"))
            .build();
  }

  private URL getResource(String path) {
    return getClass().getClassLoader().getResource(path);
  }

  @Override
  public void play(Set<Event> events) throws PlaybackException {
    for (Event event : events) {
      playEvent(event);
    }
  }

  private void playEvent(Event event) throws PlaybackException {
    URL audioUrl = getAudioUrl(event);
    if (audioUrl == null) {
      return;
    }

    try {
      // This makes unit tests tricky - a better approach might be to supply a ClipProvider to this
      // class
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
    return audioUrls.get(eventType);
  }
}
