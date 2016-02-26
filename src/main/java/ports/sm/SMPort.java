package ports.sm;

import se.sics.kompics.PortType;

public class SMPort extends PortType {

  {
    request(Command.class);
    indication(CommandReturn.class);
    //indication(CommandReturnMessage.class);
  }
}