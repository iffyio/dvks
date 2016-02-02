package ports.sm;

import se.sics.kompics.KompicsEvent;

public class ReadReturn implements KompicsEvent{

  public final int key, value;

  public ReadReturn (int key, int value) {
    this.key = key;
    this.value = value;
  }

  public String toString() {
    return "readReturn " + key + " => " + value;
  }
}