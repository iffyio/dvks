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
    subscribe(commandReturnHandler, sm_port);
  }


  Handler<Start> startHandler = new Handler<Start>() {
    public void handle(Start event) {
      //logger.info("Client started on node {}!", self);
      if (self.getIp().toString().contains(".3")) {
        //trigger(new Command(22, 4), sm_port); //read
        //trigger(new Command(21), sm_port); //read
        trigger(new Command(20), sm_port); //read
      }else if (self.getIp().toString().contains(".5")){
        trigger(new Command(21, 4), sm_port); //read
      }
    }
  };

  Handler<CommandReturn> commandReturnHandler = new Handler<CommandReturn>() {
    @Override
    public void handle(CommandReturn commandReturn) {
      logger.info("{} received {}", self, commandReturn);
      if (commandReturn.cmd.op == Op.WRITE)
        trigger(new Command(21), sm_port); //read
    }
  };

  public static class Init extends se.sics.kompics.Init<Client> {
    public final TAddress self;
    public Init(TAddress self) {
      this.self = self;
    }
  }

}
