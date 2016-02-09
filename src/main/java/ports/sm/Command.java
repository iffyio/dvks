package ports.sm;

import se.sics.kompics.KompicsEvent;

public class Command implements KompicsEvent{

  public int key, value;
  public boolean isRead = true;

  public Command (int key) {
    this.key = key;
  }

  public Command (int key, int value) {
    this(key);
    this.value = value;
    isRead = false;
  }

  public String toString() {
    if (isRead)
      return "<Read " + key + ">";
    else
      return "<Write " + key + " | " + value + ">";
  }

}

