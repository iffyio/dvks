package ports.sm;

import se.sics.kompics.KompicsEvent;

public class Read implements KompicsEvent{

  public final int key;

  public Read (int key) {
    this.key = key;
  }

  public String toString() {
    return "read " + key;
  }
}
