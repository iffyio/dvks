package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class WriteCommand extends TMessage implements Serializable{

  public final int key, value;
  private static final long serialVersionUID = 12345678L;

  public WriteCommand (TAddress src, TAddress dst, int key, int value) {
    super(src, dst, Transport.TCP);
    this.key = key;
    this.value = value;
  }

  public String toString() {
    return "writeCommand " + key + " => " + value;
  }
}

