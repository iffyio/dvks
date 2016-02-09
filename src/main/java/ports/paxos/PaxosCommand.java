package ports.paxos;


import msg.TAddress;
import msg.TMessage;
import ports.sm.Command;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;

import java.io.Serializable;
import java.util.LinkedList;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PaxosCommand that = (PaxosCommand) o;

    if (!proposer.equals(that.proposer)) return false;
    return command.equals(that.command);

  }

  @Override
  public int hashCode() {
    int result = proposer.hashCode();
    result = 31 * result + command.hashCode();
    return result;
  }
}