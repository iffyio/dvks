package ports.paxos;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class ProposeRequest extends TMessage implements Serializable{

  private static final long serialVersionUID = -136632312238L;

  public PaxosCommand command;

  public ProposeRequest(TAddress src, TAddress dst, PaxosCommand command) {
    super(src, dst, Transport.TCP);
    this.command = command;
  }
}