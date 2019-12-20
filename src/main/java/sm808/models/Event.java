package sm808.models;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * An Event is the basic building block of a track. It maps to a single sound. Currently, the sm-808
 * only supports the basic elements of a drumkit.
 */
@AllArgsConstructor
@Getter
public enum Event {
  KICK("kick", Optional.of(36), Optional.of("audio/kick.wav")),
  SNARE("snare", Optional.of(38), Optional.of("audio/snare.wav")),
  HIHAT("hihat", Optional.of(62), Optional.of("audio/hihat.wav"));

  @NonNull private final String name;
  @NonNull private final Optional<Integer> midiNoteValue;
  @NonNull private final Optional<String> audioPath;

  @Override
  public String toString() {
    return name;
  }
}
