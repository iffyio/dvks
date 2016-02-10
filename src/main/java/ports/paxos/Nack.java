package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class Nack extends TMessage implements Serializable {

  private static final long serialVersionUID = -8439839983L;

  public int pts, t;
  //pts = seq number for proposal being nacked

  public Nack(TAddress src, TAddress dst, int pts, int t) {
    super(src, dst, Transport.TCP);
    this.pts = pts; this.t = t;
  }
  public String toString() {
    return String.format("%s <Nack |%d,%d> %s", super.getSource(), pts, t, super.getDestination());
  }
}
