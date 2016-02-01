package ports.beb;

import msg.TAddress;
import se.sics.kompics.KompicsEvent;

public class BebDeliver implements KompicsEvent{
  public TAddress src;

  public BebDeliver(TAddress src) {
    this.src = src;
  }
}