package parents.client;

import components.epfd.EPFD;
import components.paxos.Paxos;
import components.sm.SM;
import main.Client;
import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.epfd.EPFDPort;
import ports.paxos.PaxosPort;
import ports.sm.SMPort;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.ChannelClosed;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;


public class ClientParent extends ComponentDefinition{

  private static final Logger logger = LoggerFactory.getLogger(ClientParent.class);
  public final TAddress self;
  public final HashSet<TAddress> nodes;

  Positive<Network> network = requires(Network.class);
  Positive<Timer> timer = requires(Timer.class);

  public ClientParent(Init init) {
    this.self = init.self;
    this.nodes = init.nodes;

    Component stateMachine = create(SM.class, new SM.Init(self, nodes));
    Component client = create(Client.class, new Client.Init(self, init.id));
    Component epfd = create(EPFD.class, new EPFD.Init(self, nodes));
    Component paxos = create(Paxos.class, new Paxos.Init(self, nodes));

    connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);
    connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
    connect(paxos.getNegative(EPFDPort.class), epfd.getPositive(EPFDPort.class), Channel.TWO_WAY);
    connect(paxos.getNegative(Network.class), network, Channel.TWO_WAY);
    connect(paxos.getNegative(Timer.class), timer, Channel.TWO_WAY);
    connect(stateMachine.getNegative(Network.class), network, Channel.TWO_WAY);
    connect(stateMachine.getNegative(PaxosPort.class), paxos.getPositive(PaxosPort.class), Channel.TWO_WAY);
    connect(client.getNegative(SMPort.class), stateMachine.getPositive(SMPort.class), Channel.TWO_WAY);
    connect(client.getNegative(Timer.class), timer, Channel.TWO_WAY);

    //logger.info("clientParent started on node {}!", self);
  }

  public static class Init extends se.sics.kompics.Init<ClientParent> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;
    public int id;
    public Init(TAddress self, int id, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
      this.id = id;
    }
  }

}