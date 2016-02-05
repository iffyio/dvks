package ports.paxos;


import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class ReadCommandReturn extends PaxosCommand implements Serializable {

  private static final long serialVersionUID = -433986008L;

  public ReadCommandReturn (TAddress src, TAddress dst, int key, int value) {
    super(src, dst, key, value);
  }

  public String toString() {
    return "readCommandReturn " + key + " => " + value;
  }
}