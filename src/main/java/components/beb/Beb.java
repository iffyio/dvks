
package components.beb;


import msg.TAddress;
import msg.TMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.beb.BebBroadcast;
import ports.beb.BebDeliver;
import ports.beb.BebPort;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.HashSet;

public class Beb extends ComponentDefinition {

  private static final Logger logger = LoggerFactory.getLogger(Beb.class);
  private TAddress self;
  private HashSet<TAddress> nodes;
  private int group;

  private Positive<Network> network = requires(Network.class);
  private Negative<BebPort> beb_port = provides(BebPort.class);

  public Beb(Init init) {
    this.self = init.self;
    this.nodes = init.nodes;

    subscribe(startHandler, control);
    subscribe(bcastHandler, beb_port);
    subscribe(netHandler, network);
  }

  private Handler<Start> startHandler = new Handler<Start>() {
    public void handle(Start event) {
      logger.info("Component Beb created at {}!", self);
    }
  };

  private Handler<BebBroadcast> bcastHandler = new Handler<BebBroadcast>() {
    public void handle(BebBroadcast event) {
      logger.info("bcasting request!");
      TMessage bebMsg = event.bebMsg;
      for (TAddress node : nodes)  {
        //if broadcast only to replication partition
        if (event.group && self.group != node.group)
          continue;
        logger.info("bcasting to {}!", node);
        trigger(bebMsg, network);
      }
    }
  };

  private Handler<TMessage> netHandler = new Handler<TMessage>() {
    public void handle(TMessage event) {
      TAddress from = event.getSource();
      logger.info("received broadcast msg from network");
      trigger(new BebDeliver(from), beb_port);
    }
  };


  public static class Init extends se.sics.kompics.Init<Beb> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }
}
