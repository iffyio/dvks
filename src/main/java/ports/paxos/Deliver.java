package ports.paxos;

import se.sics.kompics.KompicsEvent;

public class Deliver implements KompicsEvent {
  public PaxosCommand command;

  public Deliver(PaxosCommand command) {
    this.command = command;
  }
}