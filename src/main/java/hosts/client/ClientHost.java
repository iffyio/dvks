package hosts.client;

import components.beb.Beb;
import components.sm.SM;
import main.Client;
import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.beb.BebPort;
import ports.sm.SMPort;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;


public class ClientHost extends ComponentDefinition{

  private static final Logger logger = LoggerFactory.getLogger(ClientHost.class);
  public final TAddress self;
  public final HashSet<TAddress> nodes;

  public ClientHost(Init init) {
    this.self = init.self;
    this.nodes = init.nodes;

    //Component beb = create(Beb.class, new Beb.Init(self, nodes));
    Component network = create(NettyNetwork.class, new NettyInit(self));
    Component stateMachine = create(SM.class, new SM.Init(self, nodes));
    Component client = create(Client.class, new Client.Init(self));

    //connect(beb.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
    //connect(stateMachine.getNegative(BebPort.class), beb.getPositive(BebPort.class), Channel.TWO_WAY);
    connect(stateMachine.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
    connect(client.getNegative(SMPort.class), stateMachine.getPositive(SMPort.class), Channel.TWO_WAY);

    logger.info("clientParent started on node {}!", self);
  }

  public static class Init extends se.sics.kompics.Init<ClientHost> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }

}