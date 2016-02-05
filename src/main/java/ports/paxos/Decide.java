package ports.paxos;


import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

public class Decide extends TMessage {

  public PaxosCommand command;

  public Decide(TAddress src, TAddress dst, PaxosCommand command) {
    super(src, dst, Transport.TCP);
    this.command = command;
  }

  public String toString() {
    return "Decide " + command.toString();
  }
}