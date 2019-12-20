package sm808.outputdevices;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sm808.models.Event;

public class ConsoleOutputDeviceTest {
  private final ConsoleOutputDevice device = new ConsoleOutputDevice();

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @Before
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @After
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  public void testOutput() {
    device.play(ImmutableSet.of(Event.KICK));
    device.play(ImmutableSet.of());
    device.play(ImmutableSet.of(Event.HIHAT));
    device.play(ImmutableSet.of());
    device.play(ImmutableSet.of(Event.KICK, Event.SNARE));
    device.play(ImmutableSet.of());
    device.play(ImmutableSet.of(Event.HIHAT));
    device.play(ImmutableSet.of());
    device.endBar();
    device.play(ImmutableSet.of(Event.KICK));
    device.play(ImmutableSet.of());
    device.play(ImmutableSet.of(Event.HIHAT));
    device.play(ImmutableSet.of());
    device.play(ImmutableSet.of(Event.KICK, Event.SNARE));
    device.play(ImmutableSet.of());
    device.play(ImmutableSet.of(Event.HIHAT));
    device.play(ImmutableSet.of());
    device.endBar();

    String expected =
        "|kick|_|hihat|_|kick+snare|_|hihat|_|\n" + "|kick|_|hihat|_|kick+snare|_|hihat|_|\n";
    assertEquals(expected, outContent.toString());
  }
}
