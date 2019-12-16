package sm808.outputdevices;

import org.junit.Test;
import sm808.models.Event;

import static junit.framework.TestCase.assertTrue;

public class AudioOutputDeviceTest {
  @Test
  public void testGetAudioUrl() {
    AudioOutputDevice audioOutputDevice = new AudioOutputDevice();
    assertTrue(audioOutputDevice.getAudioUrl(Event.KICK).getFile().contains("audio/kick.wav"));
    assertTrue(audioOutputDevice.getAudioUrl(Event.SNARE).getFile().contains("audio/snare.wav"));
    assertTrue(audioOutputDevice.getAudioUrl(Event.HIHAT).getFile().contains("audio/hihat.wav"));
  }
}