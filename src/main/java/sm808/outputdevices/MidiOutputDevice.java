package sm808.outputdevices;

import com.google.common.collect.ImmutableMap;
import sm808.models.Event;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import java.util.Map;
import java.util.Set;

public class MidiOutputDevice implements OutputDevice {
    private static final int VELOCITY = 93;
    private static final Map<Event, Integer> MIDI_MAPPING = ImmutableMap.<Event, Integer>builder()
            .put(Event.KICK, 36)
            .put(Event.SNARE, 38)
            .put(Event.HIHAT, 62)
            .build();

    @Override
    public void play(Set<Event> events) throws PlaybackException {
        for (Event event : events) {
            try {
                play(event);
            } catch (InvalidMidiDataException | MidiUnavailableException e) {
                throw new PlaybackException(e);
            }
        }
    }

    private void play(Event event) throws InvalidMidiDataException, MidiUnavailableException {
        // Picks an arbitrary default value if no value defined.
        int noteValue = MIDI_MAPPING.getOrDefault(event, 0);
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.NOTE_ON, 0, noteValue, VELOCITY);
        // Sends to the default receiver. An improvement could be to supply the Receiver on class construction.
        MidiSystem.getReceiver().send(message, -1);
    }
}
