package sm808;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import sm808.models.Sequence;
import sm808.outputdevices.OutputDevice;

@RequiredArgsConstructor
public class Sequencer {
  @NonNull private final OutputDevice outputDevice;

  private final int tempo;

  @NonNull
  @Getter
  private Sequence sequence;

  // TODO should this be volatile?
  private int currentStep = 0;

  public void startSequence() {
    // TODO this is where we will execute each click in time
  }

  /**
   * Figures out which events need to be played right now and sends them to the output device.
   */
  @VisibleForTesting
  protected void click() {
    outputDevice.play(sequence.getEvents(currentStep));
    currentStep++;

    if (currentStep >= sequence.getSteps()) {
      outputDevice.endBar();
      currentStep = 0;
    }
  }
}
