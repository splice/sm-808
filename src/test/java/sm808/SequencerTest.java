package sm808;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sm808.models.Event;
import sm808.models.Sequence;
import sm808.outputdevices.OutputDevice;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SequencerTest {
  @Mock private OutputDevice mockOutputDevice;
  private Sequencer sequencer;

  @Before
  public void setUp() {
    sequencer = new Sequencer(mockOutputDevice, 100, new Sequence(3));
  }

  @Test
  public void testClick() {
    Sequence sequence = sequencer.getSequence();
    sequence.clear();
    sequence.addEvents(0, Event.HIHAT, Event.KICK);
    sequence.addEvents(2, Event.SNARE);

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of(Event.HIHAT, Event.KICK));

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of());

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of(Event.SNARE));
    verify(mockOutputDevice).endBar();

    // Need to reset the mock so that we forget about the first time it was called on step 0
    reset(mockOutputDevice);

    sequencer.click();
    verify(mockOutputDevice).play(ImmutableSet.of(Event.HIHAT, Event.KICK));
  }
}