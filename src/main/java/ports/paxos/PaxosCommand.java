package ports.paxos;


import msg.TAddress;
import msg.TMessage;
import ports.sm.Command;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class PaxosCommand implements Serializable, KompicsEvent{

  private static final long serialVersionUID = 43359285678L;
  public TAddress proposer;
  public Command command;

  public PaxosCommand (TAddress proposer, Command command) {
    this.command = command;
    this.proposer = proposer;
  }

  public String toString() {
    return "<PaxosCommand | " + command.toString() + " >";
  }
}