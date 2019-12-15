package sm808;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sm808.models.Event;
import sm808.models.Sequence;
import sm808.outputdevices.OutputDevice;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SequencerTest {
  @Mock private OutputDevice mockOutputDevice;

  @Test
  public void testClick() {
    // Underlying sequence should be only two steps
    int tempo = 100;
    Sequencer sequencer = new Sequencer(mockOutputDevice, tempo, 1, 2);
    Sequence sequence = sequencer.getSequence();
    sequence.clear();
    sequence.addEvents(0, Event.HIHAT, Event.KICK);

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of(Event.HIHAT, Event.KICK));

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of());
    verify(mockOutputDevice).endBar();

    // Need to reset the mock so that we forget about the first time it was called on step 0
    reset(mockOutputDevice);

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of(Event.HIHAT, Event.KICK));
  }

  @Test
  public void testComputeClickDuration() {
    // At 60 BPM, 4/4 time, and 2 subdivisions, we should have 120 clicks per minute = 500 ms per click
    assertEquals(500, new Sequencer(mockOutputDevice, 60, 4, 2).computeClickDurationMillis());
  }

  @Test
  public void testStopAndStart() throws InterruptedException {
    reset(mockOutputDevice);
    // With this configuration, we should get two clicks in one second
    Sequencer sequencer = new Sequencer(mockOutputDevice, 59, 4, 2);
    sequencer.startSequence();
    Thread.sleep(1000);
    sequencer.stopSequence();
    verify(mockOutputDevice, times(2)).play(ImmutableSet.of());
  }
}
