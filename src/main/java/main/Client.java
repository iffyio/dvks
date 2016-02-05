package main;

import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.sm.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;


public class Client extends ComponentDefinition {

  private static final Logger logger = LoggerFactory.getLogger(Client.class);
  private final TAddress self;

  //Positive<BebPort> beb_port = requires(BebPort.class);
  //Positive<Timer> timer = requires(Timer.class);
  //Positive<Network> network = requires(Network.class);
  Positive<SMPort> sm_port = requires(SMPort.class);


  public Client(Init init) {
    this.self = init.self;

    subscribe(startHandler, control);
    subscribe(readReturnHandler, sm_port);
    subscribe(writeReturnHandler, sm_port);
  }


  Handler<Start> startHandler = new Handler<Start>() {
    public void handle(Start event) {
      //logger.info("Client started on node {}!", self);
      if (self.getIp().toString().contains(".3") || self.group == 3) {
        //logger.info("trying to send read");
        trigger(new Read(21), sm_port);
        //trigger(new Write(1, 44), sm_port);
        //trigger(new Read(1), sm_port);
      }
    }
  };

  Handler<ReadReturn> readReturnHandler = new Handler<ReadReturn>() {
    @Override
    public void handle(ReadReturn rr) {
      logger.info("received {}", rr);
      //trigger(new Write(300, 355873), sm_port);
    }
  };

  Handler<WriteReturn> writeReturnHandler = new Handler<WriteReturn>() {
    @Override
    public void handle(WriteReturn wr) {
      logger.info("received {} on node {}!", wr, self);
    }
  };

  public static class Init extends se.sics.kompics.Init<Client> {
    public final TAddress self;
    public Init(TAddress self) {
      this.self = self;
    }
  }
}
