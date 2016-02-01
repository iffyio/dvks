package ports.beb;

import msg.TMessage;
import se.sics.kompics.KompicsEvent;

public class BebBroadcast implements KompicsEvent {

  public final TMessage bebMsg;
  public boolean group;

  public BebBroadcast(TMessage bebMsg, boolean group) {
    this.bebMsg = bebMsg;
    this.group = group;
  }
}
