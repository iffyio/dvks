package ports.sm;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class CommandReturnMessage extends TMessage implements Serializable{

  private static final long serialVersionUID = -1733233218L;
  public int key, value;
  public boolean isRead = true;

  public CommandReturnMessage (TAddress src, TAddress dst, int key, int value, boolean isRead) {
    super(src, dst, Transport.TCP);
    this.key = key; this.value = value;
    this.isRead = isRead;
  }

  public String toString() {
    return "readReturnMessage " + key + " => " + value;
  }
}
