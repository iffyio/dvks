package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;
import java.util.LinkedList;

public class PrepareAck extends TMessage implements Serializable{

  private static final long serialVersionUID = -9209839983L;

  public int pts, ats, al, t;
  //pts- seqNo, ats = na - seqNum accepted by acceptor
  //al - ld, t = lamport
  public LinkedList<PaxosCommand> vsuf;

  public PrepareAck(TAddress src, TAddress dst, int pts, int ats, LinkedList<PaxosCommand> vsuf,
                    int al, int t) {
    super(src, dst, Transport.TCP);
    this.pts = pts; this.ats = ats; this.al = al; this.t = t;
    this.vsuf = vsuf;
  }
}
