package ports.paxos;


import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class PaxosCommand extends TMessage implements Serializable{

  private static final long serialVersionUID = 43359285678L;
  public int key;
  public Integer value = null;

  public PaxosCommand (TAddress src, TAddress dst, int key) {
    super(src, dst, Transport.TCP);
    this.key = key;
  }
  public PaxosCommand (TAddress src, TAddress dst, int key, int value) {
    this(src, dst, key);
    this.value = value;
  }

}