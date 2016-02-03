package main;

import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.epfd.EPFDPort;
import ports.epfd.Restore;
import ports.epfd.Suspect;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;

import java.util.HashSet;


public class EPFDClient extends ComponentDefinition {

  private static final Logger logger = LoggerFactory.getLogger(EPFDClient.class);
  private final TAddress self;
  public final HashSet<TAddress> nodes, alive;

  Positive<EPFDPort> epfd_port = requires(EPFDPort.class);


  public EPFDClient(Init init) {
    this.self = init.self;
    nodes = init.nodes;
    alive = new HashSet<>(nodes);

    subscribe(startHandler, control);
    subscribe(suspectHandler, epfd_port);
    subscribe(restoreHandler, epfd_port);
  }


  Handler<Start> startHandler = new Handler<Start>() {
    public void handle(Start event) {
      logger.info("epfdClient started on node {}!", self);
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



  public static class Init extends se.sics.kompics.Init<EPFDClient> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }
}
