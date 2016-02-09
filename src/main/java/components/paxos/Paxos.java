package components.paxos;

import main.Routing;
import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.epfd.EPFDPort;
import ports.epfd.Restore;
import ports.epfd.Suspect;
import ports.paxos.*;
import ports.sm.Command;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.HashSet;
import java.util.LinkedList;

public class Paxos extends ComponentDefinition {

  private final HashSet<TAddress> nodes, alive;
  private final TAddress self;
  private final LinkedList<Command> pending;
  private static final Logger logger = LoggerFactory.getLogger(Paxos.class);

  public Paxos(Init init) {
    self = init.self;
    nodes = init.nodes;
    alive = new HashSet<>(nodes);
    pending = new LinkedList<>();


    subscribe(startHandler, control);
    subscribe(suspectHandler, epfd_port);
    subscribe(restoreHandler, epfd_port);
    subscribe(proposeHandler, paxos_port);
    subscribe(proposeRequestHandler, network);
    subscribe(decideHandler, network);
  }

  Positive<Network> network = requires(Network.class);
  Positive<EPFDPort> epfd_port = requires(EPFDPort.class);
  Negative<PaxosPort> paxos_port = provides(PaxosPort.class);

  Handler<Start> startHandler = new Handler<Start>() {
    public void handle(Start event) {
      logger.info("paxos started on node {}!", self);
    }
  };

  Handler<Propose> proposeHandler = new Handler<Propose>() {
    @Override
    public void handle(Propose propose) {
      logger.info("{} received new propose {}", self, propose.command);
      Command c = propose.command;
      int c_group = Routing.get_group(c.key);
      if (c_group != self.group) {
        for (TAddress node : nodes) {
          if (node.group == c_group)
            trigger(new ProposeRequest(self, node, c), network);
        }
      }else {
        PaxosCommand pc = new PaxosCommand(self, c);
        propose(pc);
      }
    }
  };

  private void propose(PaxosCommand pc) {
    logger.info("{} proposing {}", self, pc);
  }

  Handler<ProposeRequest> proposeRequestHandler = new Handler<ProposeRequest>() {
    @Override
    public void handle(ProposeRequest pr) {
      TAddress leader = Routing.get_leader(self.group, alive);
      if (!leader.equals(self)) {
        trigger(new ProposeRequest(pr.getSource(), leader, pr.command), network);
      }else{
        PaxosCommand pc = new PaxosCommand(pr.getSource(), pr.command);
        propose(pc);
      }
    }
  };


  Handler<Decide> decideHandler = new Handler<Decide>() {
    @Override
    public void handle(Decide decide) {
      trigger(new Deliver(decide.command), paxos_port);
    }
  };





  Handler<Suspect> suspectHandler = new Handler<Suspect>() {
    @Override
    public void handle(Suspect suspect) {
      logger.info("{}: node {} is now suspected!", self, suspect.node);
      alive.remove(suspect.node);
    }
  };

  Handler<Restore> restoreHandler = new Handler<Restore>() {
    @Override
    public void handle(Restore restore) {
      logger.info("{}: node {} is now restored!", self, restore.node);
      alive.add(restore.node);
    }
  };


  public static class Init extends se.sics.kompics.Init<Paxos> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }
}