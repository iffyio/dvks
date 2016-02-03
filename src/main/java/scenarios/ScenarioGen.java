package scenarios;

import main.Routing;
import msg.TAddress;
import parents.client.ClientParent;
import parents.epfd.EPFDParent;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;

public class ScenarioGen {
  static String addr_prefix = "192.193.0.";
  static HashSet<TAddress> nodes;
  static int port = 10000;

  static{
    nodes = new HashSet<>(6);
    for (int i = 1; i <= 6; i++) {
      try {
        InetAddress ip = InetAddress.getByName(addr_prefix + i);
        TAddress addr = new TAddress(ip, port);
        addr.group = Routing.get_group(i);
        nodes.add(addr);
      } catch (UnknownHostException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static Operation1 killNodeOp = new Operation1<KillNodeEvent, Integer>() {

    public KillNodeEvent generate(final Integer self) {

      return new KillNodeEvent() {
        TAddress selfAdr;
        {
          try{
            selfAdr = new TAddress(InetAddress.getByName(addr_prefix + self), port);
            selfAdr.group = Routing.get_group(self);
          } catch (UnknownHostException e) {
            throw new RuntimeException(e);
          }
        }

        @Override
        public Address getNodeAddress() {
          return selfAdr;
        }

        public String toString() {
          return "KillNodeOp<" + selfAdr.toString() + " group " + selfAdr.group + ">";
        }
      };
    }
  };

  static Operation1 startClientOp = new Operation1<StartNodeEvent, Integer>() {

    public StartNodeEvent generate(final Integer self) {

      return new StartNodeEvent() {
        TAddress selfAdr;
        {
          try{
            selfAdr = new TAddress(InetAddress.getByName(addr_prefix + self), port);
            selfAdr.group = Routing.get_group(self);
          } catch (UnknownHostException e) {
            throw new RuntimeException(e);
          }
        }

        @Override
        public Address getNodeAddress() {
          return selfAdr;
        }

        @Override
        public Class getComponentDefinition() {
          return ClientParent.class;
        }

        @Override
        public Init getComponentInit() {
          return new ClientParent.Init(selfAdr, nodes);
        }

        public String toString() {
          return "StartClient<" + selfAdr.toString() + " group " + selfAdr.group + ">";
        }
      };
    }
  };

  static Operation1 epfdClientOp = new Operation1<StartNodeEvent, Integer>() {

    public StartNodeEvent generate(final Integer self) {

      return new StartNodeEvent() {
        TAddress selfAdr;
        {
          try{
            selfAdr = new TAddress(InetAddress.getByName(addr_prefix + self), port);
            selfAdr.group = Routing.get_group(self);
          } catch (UnknownHostException e) {
            throw new RuntimeException(e);
          }
        }

        @Override
        public Address getNodeAddress() {
          return selfAdr;
        }

        @Override
        public Class getComponentDefinition() {
          return EPFDParent.class;
        }

        @Override
        public Init getComponentInit() {
          return new EPFDParent.Init(selfAdr, nodes);
        }

        public String toString() {
          return "StartEPFDClient<" + selfAdr.toString() + " group " + selfAdr.group + ">";
        }
      };
    }
  };


  public static SimulationScenario bebScene() {
    SimulationScenario scen = new SimulationScenario() {
      {
        SimulationScenario.StochasticProcess kill_node = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(0));
            raise(1, killNodeOp, new BasicIntSequentialDistribution(4));
          }
        };

        SimulationScenario.StochasticProcess restart_node = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(0));
            raise(1, epfdClientOp, new BasicIntSequentialDistribution(4));
          }
        };

        SimulationScenario.StochasticProcess clients = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(0));
            raise(6, startClientOp, new BasicIntSequentialDistribution(1));
          }
        };

        SimulationScenario.StochasticProcess epfdclients = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(0));
            raise(6, epfdClientOp, new BasicIntSequentialDistribution(1));
          }
        };

        epfdclients.start();
        kill_node.startAfterTerminationOf(1000, epfdclients);
        restart_node.startAfterTerminationOf(4000, epfdclients);
        terminateAfterTerminationOf(3000, restart_node);
      }
    };
    return scen;
  }
}