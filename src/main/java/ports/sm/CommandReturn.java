package ports.sm;

import se.sics.kompics.KompicsEvent;

public class CommandReturn implements KompicsEvent{

  public final int key, value;

  public CommandReturn (int key, int value) {
    this.key = key;
    this.value = value;
  }

  public String toString() {
    return "commandReturn " + key + " => " + value;
  }
}