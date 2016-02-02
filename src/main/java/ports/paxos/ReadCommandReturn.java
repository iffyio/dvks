package ports.paxos;


import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

public class ReadCommandReturn extends TMessage {

  public final int key, value;

  public ReadCommandReturn (TAddress src, TAddress dst, int key, int value) {
    super(src, dst, Transport.TCP);
    this.key = key;
    this.value = value;
  }

  public String toString() {
    return "readCommandReturn " + key + " => " + value;
  }
}