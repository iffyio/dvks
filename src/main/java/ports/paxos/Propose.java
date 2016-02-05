package ports.paxos;

import ports.sm.Command;
import se.sics.kompics.KompicsEvent;

public class Propose implements KompicsEvent {

  public final Command command;

  public Propose(Command command) {
    this.command = command;
  }

}