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


  //Positive<BebPort> beb_port = requires(BebPort.class);
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
    subscribe(readHandler, sm_port);
    subscribe(writeHandler, sm_port);
    subscribe(deliverHandler, paxos_port);
    //subscribe(readCommandHandler, network);
    //subscribe(readCommandReturnHandler, network);
    //subscribe(writeCommandHandler, network);
    //subscribe(writeCommandReturnHandler, network);
  }


  Handler<Start> startHandler = new Handler<Start>() {
    public void handle(Start event) {
      //logger.info("Client started on node {}!", self);
    }
  };

  Handler<Read> readHandler = new Handler<Read>() {
    @Override
    public void handle(Read read) {
      /*for (TAddress node : nodes) {
        if (Routing.get_group(read.key) == node.group) {
          ReadCommand rc = new ReadCommand(self, node, read.key);
          logger.info("sending {} to {}!", rc, node);
          trigger(rc, network);
        }
      }*/
      logger.info("{} trigger new propose {}", self, read);
      trigger(new Propose(read), paxos_port);
    }
  };

  Handler<Deliver> deliverHandler = new Handler<Deliver>() {
    @Override
    public void handle(Deliver deliver) {
      PaxosCommand c = deliver.command;
      logger.info("{} delivered {}", self, c);
    }
  };

  Handler<Write> writeHandler = new Handler<Write>() {
    @Override
    public void handle(Write write) {
      for (TAddress node : nodes) {
        if (Routing.get_group(write.key) == node.group) {
          WriteCommand wc = new WriteCommand(self, node, write.key, write.value);
          logger.info("sending {} to {}!", wc, node);
          trigger(wc, network);
        }
      }
    }
  };

  Handler<ReadCommand> readCommandHandler = new Handler<ReadCommand>() {
    @Override
    public void handle(ReadCommand rc) {
      logger.info("received {}! ... reading... returning...", rc);
      trigger(new ReadCommandReturn(self, rc.getSource(), rc.key, store.get(rc.key)), network);
    }
  };

  Handler<ReadCommandReturn> readCommandReturnHandler = new Handler<ReadCommandReturn>() {
    @Override
    public void handle(ReadCommandReturn rcr) {
      logger.info("received {}!", rcr);
      trigger(new ReadReturn(rcr.key, rcr.value), sm_port);
    }
  };

  Handler<WriteCommand> writeCommandHandler = new Handler<WriteCommand>() {
    @Override
    public void handle(WriteCommand wc) {
      store.put(wc.key,wc.value);
      logger.info("received {}! ... writing ... returning.. me {}.", wc, self);
      trigger(new WriteCommandReturn(self, wc.getSource(), wc.key, wc.value), network);
    }
  };

  Handler<WriteCommandReturn> writeCommandReturnHandler = new Handler<WriteCommandReturn>() {
    @Override
    public void handle(WriteCommandReturn wcr) {
      logger.info("received {}!", wcr);
      trigger(new WriteReturn(wcr.key, wcr.value), sm_port);
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
