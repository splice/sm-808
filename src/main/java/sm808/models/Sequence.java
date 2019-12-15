package sm808.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Iterator;
import java.util.Set;

/**
 * A data structure representing a sequence. Each step in the sequence maps to a set of events at that step.
 */
@AllArgsConstructor
public class Sequence {
    @Getter private final int steps;

    // A sequence is a map from a step's index to the set of events at that step.
    private final SetMultimap<Integer, Event> sequence = HashMultimap.create();

    public void addEvents(final int step, @NonNull final Event... events) {
        Preconditions.checkArgument(step < steps);
        for (Event event : events) {
            sequence.put(step, event);
        }
    }

    public Set<Event> getEvents(int step) {
        Preconditions.checkArgument(step < steps);
        return sequence.get(step);
    }

    public void clear() {
        sequence.clear();
    }

    public void clear(int step) {
        Preconditions.checkArgument(step < steps);
        sequence.get(step).clear();
    }
}
