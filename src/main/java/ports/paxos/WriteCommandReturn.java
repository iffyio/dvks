package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class WriteCommandReturn extends PaxosCommand implements Serializable{

  private static final long serialVersionUID = 12293345678L;

  public WriteCommandReturn (TAddress src, TAddress dst, int key, int value) {
    super(src, dst, key, value);
  }

  public String toString() {
    return "writeCommandReturn " + key + " => " + value;
  }
}