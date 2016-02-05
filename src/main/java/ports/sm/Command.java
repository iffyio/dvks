package ports.sm;

import se.sics.kompics.KompicsEvent;

public class Command implements KompicsEvent{

  public int key, value;

  public Command (int key) {
    this.key = key;
  }

  public Command (int key, int value) {
    this(key);
    this.value = value;
  }

}

