package ports.sm;

import msg.TAddress;

import java.io.Serializable;

public class WriteReturnMessage extends ReturnMessage implements Serializable{
  private static final long serialVersionUID = 99323321468L;

  public WriteReturnMessage (TAddress src, TAddress dst, int key, int value) {
    super (src, dst, key, value);
  }

  public String toString() {
    return "writeReturnMessage " + key + " => " + value;
  }
}