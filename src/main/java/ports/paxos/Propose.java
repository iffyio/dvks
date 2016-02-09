package ports.paxos;

import ports.sm.Command;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class Propose implements KompicsEvent, Serializable {

  public final Command command;

  public Propose(Command command) {
    this.command = command;
  }

}