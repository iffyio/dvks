package ports.epfd;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class HeartbeatReply extends TMessage implements Serializable {

  private static final long serialVersionUID = -647229566141L;

  public HeartbeatReply(TAddress src, TAddress dst) {
    super(src, dst, Transport.TCP);
  }
}