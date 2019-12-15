package sm808;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lombok.NonNull;
import sm808.models.Event;
import sm808.outputdevices.OutputDevice;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Sequencer {
  @NonNull private final OutputDevice outputDevice;
  private final int tempo;
  private final int subdivisions;

  private int steps;
  private int currentStep = 0;
  // A sequence is a map from a step's index to the set of events at that step.
  private final SetMultimap<Integer, Event> sequence = HashMultimap.create();

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
    this.steps = beatsPerSequence * this.subdivisions;
  }

  /**
   * Add the given event(s) to the sequence
   * @param step The step to add the event at
   * @param events One or more events to add
   */
  public void addEvents(final int step, @NonNull final Event... events) {
    Preconditions.checkArgument(step < steps);
    for (Event event : events) {
      sequence.put(step, event);
    }
  }

  /**
   * Gets the events at a given step
   * @param step The step
   * @return The events for this step
   */
  public Set<Event> getEvents(int step) {
    Preconditions.checkArgument(step < steps);
    return sequence.get(step);
  }

  /**
   * Clears all events from this sequencer's sequence
   */
  public void clear() {
    sequence.clear();
  }

  /**
   * Clears all events from the specified step
   * @param step The step
   */
  public void clear(int step) {
    Preconditions.checkArgument(step < steps);
    sequence.get(step).clear();
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
    outputDevice.play(sequence.get(currentStep));
    currentStep++;

    if (currentStep >= steps) {
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
