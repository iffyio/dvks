package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class Accept extends TMessage implements Serializable{

  private static final long serialVersionUID = -9285678L;

  public int pts, l, t;
  public PaxosCommand v;

  public Accept(TAddress src, TAddress dst, int pts, PaxosCommand v, int l, int t) {
    super(src, dst, Transport.TCP);
    this.l = l; this.t = t; this.pts = pts;
    this.v = v;
  }
}
