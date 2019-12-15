package sm808.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Sequence {
    private final int steps;
    // A sequence is a map from a step's index to the set of events at that step.
    private final SetMultimap<Integer, Event> sequence = HashMultimap.create();

    public void addEvent(int step, Event event) {
        Preconditions.checkArgument(step < steps);
        sequence.put(step, event);
    }
}
