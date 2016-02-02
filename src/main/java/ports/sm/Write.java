package ports.sm;

import se.sics.kompics.KompicsEvent;

public class Write implements KompicsEvent{

  public final int key, value;

  public Write (int key, int value) {
    this.key = key;
    this.value = value;
  }

  public String toString() {
    return "write " + key + " => " + value;
  }
}

