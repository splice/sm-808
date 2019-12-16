package sm808.sequencebuilders;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import sm808.ParserException;
import sm808.Sequencer;
import sm808.models.Event;
import sm808.outputdevices.ConsoleOutputDevice;
import sm808.outputdevices.OutputDevice;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleSequenceBuilder implements SequenceBuilder {
    // Defaults: 4/4 time, with one step per eighth note.
    private static final int DEFAULT_BEATS_PER_SEQUENCE = 4;
    private static final int DEFAULT_SUBDIVISIONS = 2;

    private static final ImmutableMap<Event, String> DEFAULT_LINES = ImmutableMap.<Event, String>builder()
            .put(Event.KICK, "|X|_|_|_|X|_|_|_|")
            .put(Event.SNARE, "|_|_|_|_|X|_|_|_|")
            .put(Event.HIHAT, "|_|_|X|_|_|_|X|_|")
            .build();

    private Scanner scanner = new Scanner(System.in);

    public void run() {
        // TODO get output device from user choice
        OutputDevice outputDevice = new ConsoleOutputDevice();

        System.out.println("==== SM-808 ====");
        System.out.print("Enter tempo (BPM): ");
        int tempo = scanner.nextInt();
        scanner.nextLine();

        // TODO Could get beats + subdivisions from user input, but would have to update parsing logic.
        Sequencer sequencer =
                new Sequencer(outputDevice, tempo, DEFAULT_BEATS_PER_SEQUENCE, DEFAULT_SUBDIVISIONS);

        DEFAULT_LINES.forEach((key, value) -> collectInput(key, value, sequencer));

        System.out.println("Press enter to start. Press enter again to stop.");
        scanner.nextLine();

        sequencer.startSequence();
        scanner.nextLine();

        sequencer.stopSequence();
        scanner.close();
    }

    private void collectInput(Event eventType, String defaultLine, Sequencer sequencer) {
        while (true) {
            System.out.println("Enter " + eventType + " line. Default: " + defaultLine);
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                line = defaultLine;
            }
            try {
                processLine(line, eventType, sequencer);
                return;
            } catch (ParserException e) {
                System.err.println("Error: " + e.getMessage());
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

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i)) {
                sequencer.addEvents(i, eventType);
            }
        }
    }
}
