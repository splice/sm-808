package sm808;

import lombok.AllArgsConstructor;
import sm808.models.Sequence;
import sm808.outputdevices.OutputDevice;

@AllArgsConstructor
public class Sequencer {
  private final OutputDevice outputDevice;
  private final int tempo;
  private Sequence sequence;

  public void startSequence() {
    // TODO this is where we will execute each click in time
  }

  private void click() {
    // TODO this is where we'll figure out which events need to be played right now and call play()
    // on the output device
  }
}
