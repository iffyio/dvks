package ports.sm;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class CommandReturnMessage extends TMessage implements Serializable{

  private static final long serialVersionUID = -1733233218L;
  public Command command;

  public CommandReturnMessage (TAddress src, TAddress dst, Command command) {
    super(src, dst, Transport.TCP);
    this.command = command;
  }

  public String toString() {
    return  String.format("<CRM | %s> from %s", command.toString(), super.getSource());
  }
}
