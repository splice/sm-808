package sm808.outputdevices;

import sm808.models.Event;

import java.util.Set;

public interface OutputDevice {
  /**
   * Play the specified events through this output device instantly.
   *
   * @param events The events to play. All events are played simultaneously.
   */
  void play(Set<Event> events);

  /**
   * Signals to the output device that a bar has ended and a new bar may begin. The output device
   * may choose not to handle this event.
   */
  void endBar();
}
