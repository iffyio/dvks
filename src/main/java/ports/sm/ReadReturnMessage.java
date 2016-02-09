package ports.sm;

import msg.TAddress;

import java.io.Serializable;

public class ReadReturnMessage extends ReturnMessage implements Serializable{
  private static final long serialVersionUID = -133233218L;

  public ReadReturnMessage (TAddress src, TAddress dst, int key, int value) {
    super (src, dst, key, value);
  }

  public String toString() {
    return "readReturnMessage " + key + " => " + value;
  }
}