package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class WriteCommand extends PaxosCommand implements Serializable{

  private static final long serialVersionUID = 12345678L;

  public WriteCommand (TAddress src, TAddress dst, int key, int value) {
    super(src, dst, key, value);
  }

  public String toString() {
    return "writeCommand " + key + " => " + value;
  }
}

