package sm808;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sm808.models.Event;
import sm808.outputdevices.OutputDevice;
import sm808.outputdevices.PlaybackException;

@RunWith(MockitoJUnitRunner.class)
public class SequencerTest {
  @Mock private OutputDevice mockOutputDevice;

  @Test
  public void testClick() throws PlaybackException {
    // Underlying sequence should be only two steps
    int tempo = 100;
    Sequencer sequencer = new Sequencer(mockOutputDevice, tempo, 1, 2);
    sequencer.addEvents(0, Event.HIHAT, Event.KICK);

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of(Event.HIHAT, Event.KICK));

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of());
    verify(mockOutputDevice).endBar();

    // Need to reset the mock so that we forget about the first time it was called on step 0
    reset(mockOutputDevice);

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of(Event.HIHAT, Event.KICK));

    // When output device errors, we should get an exception
    doThrow(new PlaybackException(new RuntimeException("Boom"))).when(mockOutputDevice).play(any());
    try {
      sequencer.click();
      fail();
    } catch (PlaybackException e) {
      // good!
    }
  }

  @Test
  public void testComputeClickDuration() {
    // At 60 BPM, 4/4 time, and 2 subdivisions, we should have 120 clicks per minute = 500 ms per
    // click
    assertEquals(500, new Sequencer(mockOutputDevice, 60, 4, 2).computeClickDurationMillis());
  }

  @Test
  public void testStopAndStart() throws InterruptedException, PlaybackException {
    reset(mockOutputDevice);
    // With this configuration, we should get two clicks in one second
    Sequencer sequencer = new Sequencer(mockOutputDevice, 59, 4, 2);
    sequencer.startSequence();
    Thread.sleep(1000);
    sequencer.stopSequence();
    verify(mockOutputDevice, times(2)).play(ImmutableSet.of());
  }

  @Test
  public void testEventManagement() {
    // This sequencer should have 4 steps
    Sequencer sequencer = new Sequencer(mockOutputDevice, 60, 2, 2);
    sequencer.addEvents(3, Event.SNARE);
    assertEquals(ImmutableSet.of(), sequencer.getEvents(0));
    assertEquals(ImmutableSet.of(Event.SNARE), sequencer.getEvents(3));

    sequencer.clear(3);
    assertEquals(ImmutableSet.of(), sequencer.getEvents(3));

    sequencer.addEvents(0, Event.KICK, Event.SNARE);
    sequencer.addEvents(1, Event.KICK);
    sequencer.clear(Event.KICK);
    assertEquals(ImmutableSet.of(Event.SNARE), sequencer.getEvents(0));
    assertEquals(ImmutableSet.of(), sequencer.getEvents(1));

    try {
      sequencer.getEvents(4);
      fail();
    } catch (Exception e) {
      // good! we were out of bounds
    }

    try {
      sequencer.clear(4);
      fail();
    } catch (Exception e) {
      // good! we were out of bounds
    }
  }
}
