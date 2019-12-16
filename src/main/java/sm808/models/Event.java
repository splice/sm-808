package sm808.models;

import lombok.AllArgsConstructor;

/**
 * An Event is the basic building block of a track. It maps to a single sound. Currently, the sm-808
 * only supports the basic elements of a drumkit.
 */
@AllArgsConstructor
public enum Event {
  KICK("kick"),
  SNARE("snare"),
  HIHAT("hihat");

  private String name;

  @Override
  public String toString() {
    return name;
  }
}
