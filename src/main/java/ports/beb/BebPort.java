package ports.beb;

import se.sics.kompics.PortType;

public class BebPort extends PortType {
  {
    request(BebBroadcast.class);
    indication(BebDeliver.class);
  }
}