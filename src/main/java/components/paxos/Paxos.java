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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Paxos extends ComponentDefinition {

  private final HashSet<TAddress> nodes, alive, group;
  private final TAddress self;
  private final LinkedList<PaxosCommand> pending;
  private final HashSet<PaxosCommand> proposedValues;
  private int t = 0, prepts = 0, ats = 0, al = 0, pts = 0,  pl = 0, N = 0;
  private LinkedList<PaxosCommand> av, pv;
  HashMap<TAddress, Integer> readList, accepted, decided;


  private static final Logger logger = LoggerFactory.getLogger(Paxos.class);

  public Paxos(Init init) {
    self = init.self;
    nodes = init.nodes;
    alive = new HashSet<>(nodes);
    group = new HashSet<>();
    pending = new LinkedList<>();
    av = new LinkedList<>();
    pv = new LinkedList<>();
    proposedValues = new HashSet<>();
    readList = new HashMap<>();
    accepted = new HashMap<>();
    decided = new HashMap<>();

    int g = self.group;
    for (TAddress node : nodes) {
      if (g == node.group) {
        N++;
        group.add(node);
        readList.put(node, null);
        accepted.put(node, 0);
        decided.put(node, 0);
      }
    }


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
            trigger(new ProposeRequest(self, node, c), network); //broadcast propose to other group
        }
      }else {
        TAddress leader = Routing.get_leader(c.key, alive);
        if (leader.equals(self)) {
          PaxosCommand pc = new PaxosCommand(self, c); //propose
          propose(pc);
        }else {
          trigger(new ProposeRequest(self, leader, c), network);//send proposal to leader
        }
      }
    }
  };

  private void propose(PaxosCommand v) {
    if (pts == 0) {
      t++;
      pts = t * N + self.rank;
      pv = prefix(av, al);
      pl = 0;
      proposedValues.add(v);
      readList = new HashMap<>();
      accepted = new HashMap<>();
      decided = new HashMap<>();
      for (TAddress node : group)
        trigger(new Prepare(self, node, pts, al, t), network);
    }else if (readList.size() <= N/2){
      proposedValues.add(v);
    }else if (!pv.contains(v)) {
      pv.add(v);
      for (TAddress node : group) {
        if (readList.get(node) == null)
          trigger(new Accept(self, node, pts, v, pv.size() - 1, t), network);
      }
    }
  }
  private LinkedList<PaxosCommand> prefix(LinkedList<PaxosCommand> list, int index) {
    LinkedList<PaxosCommand> l = new LinkedList<>();
    int i = 0;
    for (PaxosCommand pc : list) {
      if (i >= index)
        break;
      else
        l.add(pc);
      i++;
    }
    return l;
  }

  Handler<ProposeRequest> proposeRequestHandler = new Handler<ProposeRequest>() {
    @Override
    public void handle(ProposeRequest pr) {
      TAddress leader = Routing.get_leader(self.group, alive);
      if (!leader.equals(self)) {
        trigger(new ProposeRequest(pr.getSource(), leader, pr.command), network);
      }else{
        PaxosCommand pc = new PaxosCommand(pr.getSource(), pr.command); //using senders addr as source not necessary
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
      if (self.group == suspect.node.group)
        group.remove(suspect.node);
    }
  };

  Handler<Restore> restoreHandler = new Handler<Restore>() {
    @Override
    public void handle(Restore restore) {
      logger.info("{}: node {} is now restored!", self, restore.node);
      alive.add(restore.node);
      if (self.group == restore.node.group)
        group.add(restore.node);
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