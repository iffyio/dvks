package ports.sm;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class CommandReturn implements KompicsEvent, Serializable{

  private static final long serialVersionUID = -8837741928238L;


  public Integer key, value;
  public Command cmd;
  public boolean CAS_Success;

  public CommandReturn(Integer key, Command cmd) {
    this.key = key;
    this.cmd = cmd;
  }

  public CommandReturn (Integer key, Integer value, Command cmd) {
    this(key, cmd);
    this.value = value;
  }

  public CommandReturn (Integer key, Command cmd, boolean CAS_Success) {
    this.key = key;
    this.CAS_Success = CAS_Success;
  }

  public String toString() {
    String op_res = "";
    switch (cmd.op) {
      case READ:
        op_res = ""+value;
        break;
      case CAS:
        op_res = Boolean.toString(CAS_Success);
        break;
    }
    return String.format("<%sCommandReturn |%d %s>", cmd.op.toString(),key, op_res);
  }
}