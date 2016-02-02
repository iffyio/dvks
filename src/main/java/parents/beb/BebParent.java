package parents.beb;

import components.beb.Beb;
import main.Client;
import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.beb.BebPort;
import scenarios.ScenarioGen;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.timer.Timer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;


public class BebParent extends ComponentDefinition{

  private static final Logger logger = LoggerFactory.getLogger(BebParent.class);
  public final TAddress self;
  public final HashSet<TAddress> nodes;

  Positive<Network> network = requires(Network.class);
  Positive<Timer> timer = requires(Timer.class);

  public BebParent(Init init) {
    this.self = init.self;
    this.nodes = init.nodes;

    Component beb = create(Beb.class, new Beb.Init(self, nodes));
    connect(beb.getNegative(Network.class), network, Channel.TWO_WAY);

    Component client = create(Client.class, new Client.Init(self));
    connect(client.getNegative(BebPort.class), beb.getPositive(BebPort.class), Channel.TWO_WAY);

    //logger.info("Beb parent started on node {}!", self);
  }

  public static class Init extends se.sics.kompics.Init<BebParent> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }

}