package components.sm;
import ports.sm.*;

import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.paxos.*;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.HashMap;
import java.util.HashSet;

public class SM extends ComponentDefinition {

  private static final Logger logger = LoggerFactory.getLogger(SM.class);
  private final TAddress self;
  private final HashSet<TAddress> nodes;
  private final HashSet<Command> pending;
  private final HashMap<Integer, Integer> store;
  private int ts = 0;


  Negative<SMPort> sm_port = provides(SMPort.class);
  //Positive<Timer> timer = requires(Timer.class);
  Positive<Network> network = requires(Network.class);
  Positive<PaxosPort> paxos_port = requires(PaxosPort.class);

  public SM(Init init) {
    this.self = init.self;
    this.nodes = init.nodes;
    store = new HashMap<>();
    pending = new HashSet<>();

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
      c.ts = ++ts;
      pending.add(c);
      trigger(new Propose(c), paxos_port);
    }
  };

  Handler<Deliver> deliverHandler = new Handler<Deliver>() {
    @Override
    public void handle(Deliver deliver) {
      execute_and_reply(deliver.command);
    }
  };


  private void execute_and_reply(Command c) {
    CommandReturn cr = new CommandReturn(c.key, c);
    switch (c.op) {
      //execute
      case READ:
        cr.value = store.get(c.key);
        break;
      case CAS:
        cr.CAS_Success = compare_and_swap(c);
        break;
      case WRITE:
        store.put(c.key, c.value);
        break;
    }
    //reply
    TAddress sender = c.proposer;
    if (sender.group != self.group) {
      trigger(new CommandReturnMessage(self, sender,cr), network);
    }else if (sender.equals(self) && pending.remove(c)) {
      logger.info("{} sm-delivered {}", self, c);
      trigger(cr, sm_port);
    }
  }

  Handler<CommandReturnMessage> returnMessageHandler = new Handler<CommandReturnMessage>() {
    @Override
    public void handle(CommandReturnMessage crm) {
      CommandReturn cr = crm.commandReturn;
      if (pending.remove(cr.cmd)) {
        //logger.info("{} sm-delivered from group {} {}", self, Routing.get_group(c.key), c);
        trigger(cr, sm_port);
      }
    }
  };

  private boolean compare_and_swap(Command c) {
    Integer v = store.get(c.key);
    if (v == null || c.ref == null || Integer.compare(v, c.ref) != 0) {
      return false;
    }else{
      store.put(c.key, c.value);
      return true;
    }
  }


  public static class Init extends se.sics.kompics.Init<SM> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }
}
