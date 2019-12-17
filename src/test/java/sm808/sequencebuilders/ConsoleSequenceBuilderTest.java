package sm808.sequencebuilders;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import sm808.ParserException;
import sm808.Sequencer;
import sm808.models.Event;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ConsoleSequenceBuilderTest {
  @Mock private Sequencer mockSequencer;
  private final ConsoleSequenceBuilder builder = new ConsoleSequenceBuilder();

  @Before
  public void setUp() {
    int numSteps = 4;
    Mockito.when(mockSequencer.getNumSteps()).thenReturn(numSteps);
  }

  @Test
  public void testProcessLineHappyPath() throws ParserException {
    builder.processLine("|_|_|X|_|", Event.KICK, mockSequencer);
    verify(mockSequencer).addEvents(2, Event.KICK);

    // We could have anything between the pipes, but only a single X will register an event
    builder.processLine("|-|X|-X-|X|", Event.SNARE, mockSequencer);
    verify(mockSequencer).addEvents(1, Event.SNARE);
    verify(mockSequencer).addEvents(3, Event.SNARE);

    builder.processLine("|_|_|_|_|", Event.HIHAT, mockSequencer);
    verify(mockSequencer, never()).addEvents(anyInt(), eq(Event.HIHAT));
  }

  @Test
  public void testProcessLineInvalidLength() {
    try {
      builder.processLine("|_|_|X|_|_|", Event.KICK, mockSequencer);
      fail();
    } catch (ParserException e) {
      // good!
    }

    try {
      builder.processLine("|_|_|X|", Event.KICK, mockSequencer);
      fail();
    } catch (ParserException e) {
      // good!
    }

    verify(mockSequencer, never()).addEvents(anyInt(), eq(Event.KICK));
  }

  @Test
  public void testProcessLineBogusInput() {
    try {
      // Missing outer pipes
      builder.processLine("_|_|X|_", Event.KICK, mockSequencer);
      fail();
    } catch (ParserException e) {
      // good!
    }

    try {
      builder.processLine("bogus", Event.KICK, mockSequencer);
      fail();
    } catch (ParserException e) {
      // good!
    }

    verify(mockSequencer, never()).addEvents(anyInt(), eq(Event.KICK));
  }

  @Test
  public void testBuildDefaultLine() {
    int numBeats = 4, numSubdivisions = 2;
    assertEquals("|X|_|X|_|X|_|X|_|", builder.buildDefaultLine(Event.KICK, numBeats, numSubdivisions));
    assertEquals("|_|X|_|X|_|X|_|X|", builder.buildDefaultLine(Event.HIHAT, numBeats, numSubdivisions));
    assertEquals("|_|_|X|_|_|_|X|_|", builder.buildDefaultLine(Event.SNARE, numBeats, numSubdivisions));

    numBeats = 2;
    numSubdivisions = 4;
    assertEquals("|X|_|_|_|X|_|_|_|", builder.buildDefaultLine(Event.KICK,  numBeats, numSubdivisions));
    assertEquals("|_|_|_|_|X|_|_|_|", builder.buildDefaultLine(Event.SNARE, numBeats, numSubdivisions));
    assertEquals("|_|_|X|_|_|_|X|_|", builder.buildDefaultLine(Event.HIHAT, numBeats, numSubdivisions));


    numBeats = 5;
    numSubdivisions = 1;
    assertEquals("|X|X|X|X|X|", builder.buildDefaultLine(Event.KICK, numBeats, numSubdivisions));
    assertEquals("|_|X|_|X|_|", builder.buildDefaultLine(Event.SNARE, numBeats, numSubdivisions));
    assertEquals("|X|X|X|X|X|", builder.buildDefaultLine(Event.HIHAT, numBeats, numSubdivisions));

    numBeats = 3;
    numSubdivisions = 3;
    assertEquals("|X|_|_|X|_|_|X|_|_|", builder.buildDefaultLine(Event.KICK, numBeats, numSubdivisions));
    assertEquals("|_|_|_|X|_|_|_|_|_|", builder.buildDefaultLine(Event.SNARE, numBeats, numSubdivisions));
    assertEquals("|_|_|X|_|_|X|_|_|X|", builder.buildDefaultLine(Event.HIHAT, numBeats, numSubdivisions));

    numBeats = 2;
    numSubdivisions = 5;
    assertEquals("|X|_|_|_|_|X|_|_|_|_|", builder.buildDefaultLine(Event.KICK, numBeats, numSubdivisions));
    assertEquals("|_|_|_|_|_|X|_|_|_|_|", builder.buildDefaultLine(Event.SNARE, numBeats, numSubdivisions));
    assertEquals("|_|_|_|X|_|_|_|_|X|_|", builder.buildDefaultLine(Event.HIHAT, numBeats, numSubdivisions));
  }
}