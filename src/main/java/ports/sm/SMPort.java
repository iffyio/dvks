package ports.sm;

import se.sics.kompics.PortType;

public class SMPort extends PortType {

  {
    request(Read.class);
    request(Write.class);
    indication(ReadReturn.class);
    indication(WriteReturn.class);
  }
}