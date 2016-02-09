package ports.paxos;


import msg.TAddress;
import msg.TMessage;
import ports.sm.Command;
import se.sics.kompics.network.Transport;

public class Decide extends TMessage {

  public Command command;

  public Decide(TAddress src, TAddress dst, Command command) {
    super(src, dst, Transport.TCP);
    this.command = command;
  }

  public String toString() {
    return "Decide " + command.toString();
  }
}