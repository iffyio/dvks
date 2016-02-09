package ports.paxos;

import ports.sm.Command;
import se.sics.kompics.KompicsEvent;

public class Deliver implements KompicsEvent {
  public Command command;

  public Deliver(Command command) {
    this.command = command;
  }
}