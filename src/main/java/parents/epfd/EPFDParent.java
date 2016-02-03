package parents.epfd;

import components.epfd.EPFD;
import main.EPFDClient;
import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.epfd.EPFDPort;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;


public class EPFDParent extends ComponentDefinition{

  private static final Logger logger = LoggerFactory.getLogger(EPFDParent.class);
  public final TAddress self;
  public final HashSet<TAddress> nodes;

  Positive<Network> network = requires(Network.class);
  Positive<Timer> timer = requires(Timer.class);

  public EPFDParent(Init init) {
    this.self = init.self;
    this.nodes = init.nodes;

    Component epfd = create(EPFD.class, new EPFD.Init(self, nodes));
    Component epfd_client = create(EPFDClient.class, new EPFDClient.Init(self, nodes));

    connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
    connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);
    connect(epfd_client.getNegative(EPFDPort.class), epfd.getPositive(EPFDPort.class), Channel.TWO_WAY);

    logger.info("epfdParent started on node {}!", self);
  }


  public static class Init extends se.sics.kompics.Init<EPFDParent> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }

}