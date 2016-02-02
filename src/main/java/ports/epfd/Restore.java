package ports.epfd;

import msg.TAddress;
import se.sics.kompics.KompicsEvent;

public class Restore implements KompicsEvent {

  public final TAddress node;

  public Restore(TAddress node) {
    this.node = node;
  }
}