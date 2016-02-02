package ports.epfd;

import msg.TAddress;
import se.sics.kompics.KompicsEvent;

public class Suspect implements KompicsEvent {

  public final TAddress node;

  public Suspect(TAddress node) {
    this.node = node;
  }
}