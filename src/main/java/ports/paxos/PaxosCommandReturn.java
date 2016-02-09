package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class PaxosCommandReturn extends TMessage implements Serializable{

  private static final long serialVersionUID = 12293345678L;

  public int key, value;
  public boolean isRead;

  public PaxosCommandReturn (TAddress src, TAddress dst, int key, int value, boolean isRead) {
    super(src, dst, Transport.TCP);
    this.key = key; this.value = value; this.isRead = isRead;
  }

  public String toString() {
    return "writeCommandReturn " + key + " => " + value;
  }
}
