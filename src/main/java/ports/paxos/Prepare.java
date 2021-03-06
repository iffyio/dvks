package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class Prepare extends TMessage implements Serializable{

  private static final long serialVersionUID = 4329839983L;
  public int pts, al, t;
  //pts - proposal timestamp/seq number
  //al = ld - length of proposer's decided
  //t = lamport

  public Prepare(TAddress src, TAddress dst, int pts, int al, int t) {
    super(src, dst, Transport.TCP);
    this.pts = pts; this.al = al; this.t = t;
  }

  public String toString() {
    return String.format("%s <Prepare |%d,%d,%d> %s", super.getSource(), pts, al, t, super.getDestination());
  }
}
