package sm808.outputdevices;

import sm808.models.Event;

import java.util.Set;

public interface OutputDevice {
    /**
     * Play the specified events through this output device instantly.
     * @param events The events to play. All events are played simultaneously.
     */
    void play(Set<Event> events);
}