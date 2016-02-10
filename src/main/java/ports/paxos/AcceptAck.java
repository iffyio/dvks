package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class AcceptAck extends TMessage implements Serializable{

  private static final long serialVersionUID = 720939993L;

  public int pts, l, t; //l == length of q's accepted values av

  public AcceptAck(TAddress src, TAddress dst, int pts, int l, int t) {
    super(src, dst, Transport.TCP);
    this.pts = pts; this.l = l; this.t = t;
  }

  public String toString() {
    return String.format("%s <Accept |%d,%d,%d> %s", super.getSource(), pts, l, t, super.getDestination());
  }
}
