package ports.sm;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class CommandReturnMessage extends TMessage implements Serializable{

  private static final long serialVersionUID = -1733233218L;
  public CommandReturn commandReturn;

  public CommandReturnMessage (TAddress src, TAddress dst, CommandReturn commandReturn) {
    super(src, dst, Transport.TCP);
    this.commandReturn = commandReturn;
  }

  public String toString() {
    return  String.format("<CRM | %s> from %s", commandReturn.toString(), super.getSource());
  }
}
