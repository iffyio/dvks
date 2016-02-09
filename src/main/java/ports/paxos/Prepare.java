package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class Prepare extends TMessage implements Serializable{

  private static final long serialVersionUID = 4329839983L;
  public int pts, al, t;

  public Prepare(TAddress src, TAddress dst, int pts, int al, int t) {
    super(src, dst, Transport.TCP);
    this.pts = pts; this.al = al; this.t = t;
  }
}
