package sm808.sequencebuilders;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import sm808.ParserException;
import sm808.Sequencer;
import sm808.models.Event;
import sm808.outputdevices.AudioOutputDevice;
import sm808.outputdevices.ConsoleOutputDevice;
import sm808.outputdevices.MidiOutputDevice;
import sm808.outputdevices.OutputDevice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleSequenceBuilder implements SequenceBuilder {
  // Defaults: 4/4 time, with one step per eighth note.
  private static final int DEFAULT_BEATS_PER_SEQUENCE = 4;
  private static final int DEFAULT_SUBDIVISIONS = 2;

  private static final ImmutableMap<Event, String> DEFAULT_LINES =
      ImmutableMap.<Event, String>builder()
          .put(Event.KICK, "|X|_|_|_|X|_|_|_|")
          .put(Event.SNARE, "|_|_|_|_|X|_|_|_|")
          .put(Event.HIHAT, "|_|_|X|_|_|_|X|_|")
          .build();

  private Scanner scanner = new Scanner(System.in);

  public void run() {
    System.out.println("==== SM-808 ====");
    System.out.print("Enter tempo (BPM): ");
    int tempo = scanner.nextInt();
    scanner.nextLine();

    OutputDevice outputDevice = null;
    // When the sequence is being output to console, we don't want to prompt the user for input -
    // this creates a visual mess. But for other output types we can allow users to reprogram the
    // sequence on the fly.
    boolean interactiveRunMode = false;
    while (outputDevice == null) {
      System.out.println("Select an output device (1-3):");
      System.out.println("  [1] Console");
      System.out.println("  [2] Audio");
      System.out.println("  [3] MIDI");

      int choice = scanner.nextInt();
      scanner.nextLine();
      switch (choice) {
        case 1:
          outputDevice = new ConsoleOutputDevice();
          break;
        case 2:
          outputDevice = new AudioOutputDevice();
          interactiveRunMode = true;
          break;
        case 3:
          outputDevice = new MidiOutputDevice();
          interactiveRunMode = true;
          break;
        default:
          System.err.println("Choice must be between 1 and 3");
      }
    }

    // TODO Could get beats + subdivisions from user input, but would have to update parsing logic.
    Sequencer sequencer =
        new Sequencer(outputDevice, tempo, DEFAULT_BEATS_PER_SEQUENCE, DEFAULT_SUBDIVISIONS);

    if (interactiveRunMode) {
      sequencer.startSequence();
      Map<Event, String> defaultLines = DEFAULT_LINES;
      while (true) {
        // Keeps track of the last entered lines, starting with the defaults.
        defaultLines = defaultLines.entrySet()
          .stream()
          .collect(Collectors.toMap(Map.Entry::getKey, entry -> collectInput(entry.getKey(), entry.getValue(), sequencer)));
        System.out.println("Press enter to reprogram. Press Ctrl-C to quit.");
        scanner.nextLine();
      }

    } else {
      DEFAULT_LINES.forEach((key, value) -> collectInput(key, value, sequencer));

      System.out.println("Press enter to start. Press enter again to stop.");
      scanner.nextLine();

      sequencer.startSequence();
      scanner.nextLine();

      sequencer.stopSequence();
      scanner.close();
    }
  }

  private String collectInput(Event eventType, String defaultLine, Sequencer sequencer) {
    while (true) {
      System.out.println("Enter " + eventType + " line. Default: " + defaultLine);
      String line = scanner.nextLine();
      if (line.isEmpty()) {
        line = defaultLine;
      }
      try {
        processLine(line, eventType, sequencer);
        return line;
      } catch (ParserException e) {
        System.err.println("Error: " + e.getMessage());
        scanner.nextLine();
      }
    }
  }

  @VisibleForTesting
  protected void processLine(String line, Event eventType, Sequencer sequencer)
      throws ParserException {
    List<Boolean> list =
        Arrays.stream(line.split("\\|"))
            .skip(1) // Drop anything before the first pipe
            .map(value -> value.equals("X"))
            .collect(Collectors.toList());
    if (list.size() != sequencer.getNumSteps()) {
      throw new ParserException(
          "Expected " + sequencer.getNumSteps() + " steps, got " + list.size());
    }

    sequencer.clear(eventType);
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i)) {
        sequencer.addEvents(i, eventType);
      }
    }
  }

  @VisibleForTesting
  protected String buildDefaultLine(Event eventType, int numBeats, int numSubdivisions) {
    int numSteps = numBeats * numSubdivisions;
    StringBuilder sb = new StringBuilder();
    sb.append('|');
    for (int i = 0; i < numSteps; i++) {
      // See unit test for examples of default patterns
      if (eventType == Event.KICK && i % numSubdivisions == 0) {
        sb.append("X|");
      } else if (eventType == Event.SNARE && i % (2 * numSubdivisions) - numSubdivisions == 0) {
        sb.append("X|");
      } else if (eventType == Event.HIHAT && (i - Math.ceil(numSubdivisions / 2.0)) % numSubdivisions == 0) {
        sb.append("X|");
      } else {
        sb.append("_|");
      }
    }
    return sb.toString();
  }
}
