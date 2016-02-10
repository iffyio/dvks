package ports.sm;

import se.sics.kompics.KompicsEvent;

public class CommandReturn implements KompicsEvent{

  public final Integer key, value;
  public boolean isRead;

  public CommandReturn (Integer key, Integer value, boolean isRead) {
    this.key = key;
    this.value = value;
    this.isRead = isRead;
  }

  public String toString() {
    return String.format("<%sCommandReturn |%d,%d>", (isRead? "read" : "write"), key, value);
  }
}