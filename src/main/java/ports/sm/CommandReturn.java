package ports.sm;

import se.sics.kompics.KompicsEvent;

public class CommandReturn implements KompicsEvent{

  public final int key, value;
  public boolean isRead;

  public CommandReturn (int key, int value, boolean isRead) {
    this.key = key;
    this.value = value;
    this.isRead = isRead;
  }

  public String toString() {
    if (isRead)
      return "<ReadCommandReturn " + key + ">";
    else
      return "<WriteCommandReturn " + key + " | " + value + ">";
  }
}