package components.sm;

import main.Routing;
import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.paxos.*;
import ports.sm.*;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.HashMap;
import java.util.HashSet;

public class SM extends ComponentDefinition {

  private static final Logger logger = LoggerFactory.getLogger(SM.class);
  private final TAddress self;
  private final HashSet<TAddress> nodes;
  private final HashMap<Integer, Integer> store;


  Negative<SMPort> sm_port = provides(SMPort.class);
  //Positive<Timer> timer = requires(Timer.class);
  Positive<Network> network = requires(Network.class);
  Positive<PaxosPort> paxos_port = requires(PaxosPort.class);

  public SM(Init init) {
    this.self = init.self;
    this.nodes = init.nodes;
    store = new HashMap<>();

    if (self.group % 2 == 0)
      for (int i = 2; i <= 20; i += 2)
        store.put(i, i * 4);
    else
      for (int i = 1; i < 20; i += 2)
        store.put(i, i * 4);


    subscribe(startHandler, control);
    subscribe(commandHandler, sm_port);
    subscribe(deliverHandler, paxos_port);
    subscribe(returnMessageHandler, network);
  }


  Handler<Start> startHandler = new Handler<Start>() {
    public void handle(Start event) {
      //logger.info("Client started on node {}!", self);
    }
  };

  Handler<Command> commandHandler = new Handler<Command>() {
    @Override
    public void handle(Command c) {
      logger.info("{} trigger new propose {}", self, c);
      c.proposer = self;
      trigger(new Propose(c), paxos_port);
    }
  };

  Handler<Deliver> deliverHandler = new Handler<Deliver>() {
    @Override
    public void handle(Deliver deliver) {
      Command c = deliver.command;
      TAddress sender = c.proposer;
      if (!c.isRead)
        store.put(c.key, c.value);

      if (sender.group != self.group)
        send_return_message(c);
      if (sender.equals(self)) {
        logger.info("{} delivered {}", self, c);
        trigger(new CommandReturn(c.key, store.get(c.key), c.isRead), sm_port);
      }
    }
  };

  private void send_return_message(Command c) {
    TAddress sender = c.proposer;
    boolean isRead = c.isRead;
    logger.info("{} sends {} to {}", self, c, sender);
    trigger(new CommandReturnMessage(self, sender, c.key, store.get(c.key), isRead), network);
  }

  Handler<CommandReturnMessage> returnMessageHandler = new Handler<CommandReturnMessage>() {
    @Override
    public void handle(CommandReturnMessage crm) {
      logger.info("{} delivered {}", self, crm);
      trigger(new CommandReturn(crm.key, crm.value, crm.isRead), sm_port);
    }
  };


  public static class Init extends se.sics.kompics.Init<SM> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }
}
