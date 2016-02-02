package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class ReadCommand extends TMessage implements Serializable{

  public final int key;
  private static final long serialVersionUID = -1332345678L;

  public ReadCommand (TAddress src, TAddress dst, int key) {
    super(src, dst, Transport.TCP);
    this.key = key;
  }

  public String toString() {
    return "readCommand " + key;
  }
}
