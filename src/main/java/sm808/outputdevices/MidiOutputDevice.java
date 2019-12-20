package sm808.outputdevices;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import sm808.models.Event;

public class MidiOutputDevice implements OutputDevice {
  private static final int VELOCITY = 93;
  private static final Map<Event, Integer> MIDI_MAPPING =
      ImmutableMap.<Event, Integer>builder()
          .put(Event.KICK, 36)
          .put(Event.SNARE, 38)
          .put(Event.HIHAT, 62)
          .build();

  private final Map<Event, ShortMessage> midiMessages;

  public MidiOutputDevice() {
    ImmutableMap.Builder<Event, ShortMessage> builder = ImmutableMap.builder();

    for (Map.Entry<Event, Integer> entry : MIDI_MAPPING.entrySet()) {
      try {
        builder.put(
            entry.getKey(), new ShortMessage(ShortMessage.NOTE_ON, 0, entry.getValue(), VELOCITY));
      } catch (InvalidMidiDataException e) {
        // We won't be able to play this event since its MIDI mapping is poorly configured.
        e.printStackTrace();
      }
    }
    midiMessages = builder.build();
  }

  @Override
  public void play(Set<Event> events) throws PlaybackException {
    for (Event event : events) {
      try {
        play(event);
      } catch (MidiUnavailableException e) {
        throw new PlaybackException(e);
      }
    }
  }

  private void play(Event event) throws MidiUnavailableException {
    ShortMessage message = midiMessages.get(event);
    if (message == null) return;
    // Sends to the default receiver. An improvement could be to supply the Receiver on class
    // construction.
    MidiSystem.getReceiver().send(message, -1);
  }
}
