package components.epfd;

import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.epfd.*;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.*;

import java.util.HashSet;
import java.util.UUID;

public class EPFD extends ComponentDefinition {

  private static final Logger logger = LoggerFactory.getLogger(EPFD.class);
  private TAddress self;
  private HashSet<TAddress> nodes, alive, suspected;
  private long delay = 500;
  private final int delta = 100;

  Negative<EPFDPort> epfl_port = provides(EPFDPort.class);
  Positive<Network> network = requires(Network.class);
  Positive<Timer> timer = requires(Timer.class);

  private UUID timerId;

  public EPFD(Init init) {
    self = init.self;
    nodes = init.nodes;

    subscribe(startHandler, control);
    subscribe(timeoutHandler, timer);
    subscribe(hbreqHandler, network);
    subscribe(hbrepHandler, network);
  }

  Handler<Start> startHandler = new Handler<Start>() {
    @Override
    public void handle(Start start) {
      alive = new HashSet<>(nodes);
      suspected = new HashSet<>();
      //logger.info("started epfd on {}", self);

      set_timer();
    }
  };


  Handler<HeartbeatTimeout> timeoutHandler = new Handler<HeartbeatTimeout>() {
    @Override
    public void handle(HeartbeatTimeout heartbeatTimeout) {
      HashSet<TAddress> resurrected_nodes = new HashSet<>(alive);
      resurrected_nodes.retainAll(alive);

      if (!resurrected_nodes.isEmpty())
        delay = delay + delta;

      for (TAddress node : nodes) {
        if (!suspected.contains(node) &&
                !alive.contains(node)) {
          suspected.add(node);
          trigger(new Suspect(node), epfl_port);
        }
        else if (alive.contains(node) && suspected.contains(node)){
          suspected.remove(node);
          trigger(new Restore(node), epfl_port);
        }

        trigger(new HeartbeatRequest(self, node), network);
      }
      alive = new HashSet<>();
      set_timer();
    }
  };

  private void set_timer() {
    ScheduleTimeout st = new ScheduleTimeout(delay);
    HeartbeatTimeout timeout = new HeartbeatTimeout(st);
    st.setTimeoutEvent(timeout);
    trigger(st, timer);
    timerId = timeout.getTimeoutId();
  }

  public void tearDown() {
    trigger(new CancelPeriodicTimeout(timerId), timer);
  }

  Handler<HeartbeatRequest> hbreqHandler = new Handler<HeartbeatRequest>() {
    @Override
    public void handle(HeartbeatRequest hbreq) {
      //logger.info("[{}] heartbeat request from {}", self, hbreq.getSource());
      trigger(new HeartbeatReply(self, hbreq.getSource()), network);
    }
  };

  Handler<HeartbeatReply> hbrepHandler = new Handler<HeartbeatReply>() {
    @Override
    public void handle(HeartbeatReply hbrep) {
      //logger.info("[{}] heartbeat reply to {}", self, hbrep.getSource());
      alive.add(hbrep.getSource());
    }
  };

  public static class HeartbeatTimeout extends Timeout {
    public HeartbeatTimeout(ScheduleTimeout st) {
      super(st);
    }
  }

  public static class Init extends se.sics.kompics.Init<EPFD> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }
}