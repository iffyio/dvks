package main;

import msg.TAddress;
import msg.TMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class Ping extends TMessage implements Serializable{

  private static final long serialVersionUID = 752647229566141L;

  public Ping(TAddress src, TAddress dst){
    super(src, dst, Transport.TCP);
  }
}