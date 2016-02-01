package hosts.beb;

import components.beb.Beb;
import main.Client;
import msg.TAddress;
import ports.beb.BebPort;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;

import java.util.HashSet;

public class BebHost extends ComponentDefinition{

  public BebHost(Init init){
    TAddress self = init.self;
    HashSet<TAddress> nodes = init.nodes;
    Component beb = create(Beb.class, new Beb.Init(self, nodes));
    Component network = create(NettyNetwork.class, new NettyInit(self));
    Component client = create(Client.class, new Client.Init(self));

    connect(beb.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
    connect(client.getNegative(BebPort.class), beb.getPositive(BebPort.class), Channel.TWO_WAY);
  }

  public static class Init extends se.sics.kompics.Init<BebHost> {
    public final TAddress self;
    public final HashSet<TAddress> nodes;

    public Init(TAddress self, HashSet<TAddress> nodes) {
      this.self = self;
      this.nodes = nodes;
    }
  }
}