package ports.paxos;


import msg.TAddress;
import msg.TMessage;
import ports.sm.Command;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class Decide extends TMessage implements Serializable{

  private static final long serialVersionUID = 6609839983L;

  public int pts, l, t;//l = length of chosen sequence

  public Decide(TAddress src, TAddress dst, int pts, int l, int t) {
    super(src, dst, Transport.TCP);
    this.pts = pts; this.l = l; this.t = t;
  }

  public String toString() {
    return super.getSource() + "<Decide |" + pts + "," + l + "," + t + ">" + super.getDestination();
  }
}