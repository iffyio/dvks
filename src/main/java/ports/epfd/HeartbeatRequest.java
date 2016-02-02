package ports.epfd;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class HeartbeatRequest extends TMessage implements Serializable {

  private static final long serialVersionUID = 647229141L;

  public HeartbeatRequest(TAddress src, TAddress dst) {
    super(src, dst, Transport.TCP);
  }
}