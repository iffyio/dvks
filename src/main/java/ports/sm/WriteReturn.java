package ports.sm;

import se.sics.kompics.KompicsEvent;

public class WriteReturn implements KompicsEvent{

  public final int key, value;

  public WriteReturn (int key, int value) {
    this.key = key;
    this.value = value;
  }

  public String toString() {
    return "writeReturn " + key + " => " + value;
  }
}