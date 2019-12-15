package sm808;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import lombok.NonNull;
import sm808.models.Sequence;
import sm808.outputdevices.OutputDevice;

import java.util.Timer;
import java.util.TimerTask;

public class Sequencer {
  @NonNull private final OutputDevice outputDevice;

  /** The tempo, in BPM */
  private final int tempo;

  private final int subdivisions;
  @Getter private Sequence sequence;

  private int currentStep = 0;
  private Timer timer = new Timer();

  /**
   * Creates a new Sequencer.
   *
   * @param outputDevice The OutputDevice to use
   * @param tempo The tempo in BPM
   * @param beatsPerSequence The number of beats in this sequence. This corresponds to the beat
   *     value of the specified * tempo. For example, in 4/4 time we'd have 4 beats (quarter notes)
   *     per sequence. In 6/8 time * we'd have 2 beats (dotted quarters) per sequence.
   * @param subdivisions The number of subdivisions in a beat. For example, 2 subdivisions in 4/4
   *     time will result in 8 * steps per sequence, with each step representing an eighth note.
   */
  public Sequencer(
      @NonNull OutputDevice outputDevice, int tempo, int beatsPerSequence, int subdivisions) {
    this.outputDevice = outputDevice;
    this.tempo = tempo;
    this.subdivisions = subdivisions;
    this.sequence = new Sequence(beatsPerSequence * this.subdivisions);
  }

  /** Starts looping the sequence. */
  public void startSequence() {
    timer.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            click();
          }
        },
        0,
        computeClickDurationMillis());
  }

  /** Stops looping the sequence. */
  public void stopSequence() {
    timer.cancel();
  }

  /** Figures out which events need to be played right now and sends them to the output device. */
  @VisibleForTesting
  protected void click() {
    outputDevice.play(sequence.getEvents(currentStep));
    currentStep++;

    if (currentStep >= sequence.getSteps()) {
      outputDevice.endBar();
      currentStep = 0;
    }
  }

  @VisibleForTesting
  protected int computeClickDurationMillis() {
    // (ms / min) / (subdivisions * beats / min) = ms / beat
    return (60 * 1000) / (tempo * subdivisions);
  }
}
