package main;

import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.beb.BebBroadcast;
import ports.beb.BebDeliver;
import ports.beb.BebPort;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public class Client extends ComponentDefinition {

  private static final Logger logger = LoggerFactory.getLogger(Client.class);
  private final TAddress self;

  public Client(Init init) {
    this.self = init.self;

    subscribe(startHandler, control);
    subscribe(bebHandler, beb_port);
  }

  Positive<BebPort> beb_port = requires(BebPort.class);
  Positive<Timer> timer = requires(Timer.class);
  Positive<Network> network = requires(Network.class);

  Handler<Start> startHandler = new Handler<Start>() {
    public void handle(Start event) {
      //logger.info("Client started on node {}!", self);
      if (self.getIp().toString().contains(".3")) {
        logger.info("trying to broadcast ping");
        trigger(new BebBroadcast(new Ping(self, self), true), beb_port);
      }
    }
  };

  private Handler<BebDeliver> bebHandler = new Handler<BebDeliver>() {
    @Override
    public void handle(BebDeliver bebDeliver) {
      logger.info("Client on node {} bebDelivered a msg!", self);
    }
  };

  public static class Init extends se.sics.kompics.Init<Client> {
    public final TAddress self;
    public Init(TAddress self) {
      this.self = self;
    }
  }
}
