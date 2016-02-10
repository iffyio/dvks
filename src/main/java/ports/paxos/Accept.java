package ports.paxos;

import main.Routing;
import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;
import java.util.LinkedList;

public class Accept extends TMessage implements Serializable{

  private static final long serialVersionUID = -9285678L;

  public int pts, l, t;
  public LinkedList<PaxosCommand> vsuf;

  public Accept(TAddress src, TAddress dst, int pts, LinkedList<PaxosCommand> vsuf, int l, int t) {
    super(src, dst, Transport.TCP);
    this.l = l; this.t = t; this.pts = pts;
    this.vsuf = vsuf;
  }

  public String toString() {
    return String.format("%s <Accept |%d,%s,%d,%d> %s", super.getSource(), pts, Routing.vsuf_to_s(vsuf), l, t, super.getDestination());
  }
}
