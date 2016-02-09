
package ports.sm;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class ReturnMessage extends TMessage implements Serializable{

  private static final long serialVersionUID = -13323321468L;
  public int key, value;

  public ReturnMessage(TAddress src, TAddress dst, int key, int value) {
    super(src, dst, Transport.TCP);
    this.key = key; this.value = value;
  }
}