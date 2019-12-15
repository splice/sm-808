package sm808.outputdevices;

import sm808.models.Event;

import java.util.Set;
import java.util.stream.Collectors;

public class ConsoleOutputDevice implements OutputDevice {
    @Override
    public void play(Set<Event> events) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        if (events.isEmpty()) {
            sb.append("_");
        } else {
            sb.append(events.stream().map(this::eventToString).collect(Collectors.joining("+")));
        }
        System.out.print(sb.toString());
    }

    @Override
    public void endBar() {
        System.out.println("|");
    }

    private String eventToString(Event event) {
        switch(event) {
            case KICK: return "kick";
            case SNARE: return "snare";
            case HIHAT: return "hihat";
            default: return event.toString();
        }
    }
}
