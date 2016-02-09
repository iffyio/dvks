package ports.sm;

import msg.TAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class Command implements KompicsEvent, Serializable{

  private static final long serialVersionUID = 4321928238L;

  public int key, value;
  public boolean isRead = true;
  public TAddress proposer;

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

  @Override
  public int hashCode() {
    int result = key;
    result = 31 * result + value;
    result = 31 * result + (isRead ? 1 : 0);
    return result;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Command c = (Command) o;
    return this.key == c.key && value == c.value && isRead == c.isRead;
  }

}

