package ports.sm;

import msg.TAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class Command implements KompicsEvent, Serializable{

  private static final long serialVersionUID = 4321928238L;

  public Integer key, value, ref;
  public TAddress proposer;
  public int ts;
  public Op op;

  public Command (Integer key) {
    this.key = key;
    op = Op.READ;
  }

  public Command (Integer key, Integer value) {
    this(key);
    this.value = value;
    op = Op.WRITE;
  }

  public Command (Integer key, Integer value, Integer ref) {
    this(key, value);
    this.ref = ref;
    op = Op.CAS;
  }

  public String toString() {
    switch (op) {
      case READ:
        return "<Read " + key + ">";
      case WRITE:
        return "<Write " + key + " | " + value + ">";
      case CAS:
        return "<CAS " + key + " | " + value + ", " + ref + ">";
      default:
        return "Invalid Operation";
    }
  }

  @Override
  public int hashCode() {
    int result = key;
    result = 31 * result + op.hashCode();
    result = 31 * result + ts;
    result = result + proposer.hashCode();
    return result;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Command c = (Command) o;
    /*if (this.value == null && c.value != null) return false;
    if (this.value != null && !this.value.equals(c.value)) return false;
    return isRead == c.isRead && this.key.equals(c.key)
            &&this.proposer.equals(c.proposer) && this.ts == c.ts;*/
    return this.proposer.equals(c.proposer) && this.ts == c.ts;
  }

}

