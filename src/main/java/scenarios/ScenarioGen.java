package scenarios;

import main.Client;
import msg.TAddress;
import parents.beb.BebParent;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
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
        addr.group = i % 2 == 0 ? 1 : 2;
        nodes.add(addr);
      } catch (UnknownHostException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static Operation1 startClientOp = new Operation1<StartNodeEvent, Integer>() {

    public StartNodeEvent generate(final Integer self) {

      return new StartNodeEvent() {
        TAddress selfAdr;
        {
          try{
            selfAdr = new TAddress(InetAddress.getByName(addr_prefix + self), port);
            selfAdr.group = self % 2 == 0? 1 : 2;
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
          return Client.class;
        }

        @Override
        public Init getComponentInit() {
          return new Client.Init(selfAdr);
        }

        public String toString() {
          return "StartClient<" + selfAdr.toString() + " group " + selfAdr.group + ">";
        }
      };
    }
  };

  static Operation1 startBebOp = new Operation1<StartNodeEvent, Integer>() {

    public StartNodeEvent generate(final Integer self) {

      return new StartNodeEvent() {
        TAddress selfAdr;
        {
          try{
            selfAdr = new TAddress(InetAddress.getByName(addr_prefix + self), port);
            selfAdr.group = self % 2 == 0? 1 : 2;
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
          return BebParent.class;
        }

        @Override
        public Init getComponentInit() {
          return new BebParent.Init(selfAdr, nodes);
        }

        public String toString() {
          return "StartBebParent<" + selfAdr.toString() + " group " + selfAdr.group + ">";
        }
      };
    }
  };

  public static SimulationScenario bebScene() {
    SimulationScenario scen = new SimulationScenario() {
      {
        SimulationScenario.StochasticProcess bebParent = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(1000));
            raise(6, startBebOp, new BasicIntSequentialDistribution(1));
          }
        };

        SimulationScenario.StochasticProcess clients = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(1000));
            raise(6, startClientOp, new BasicIntSequentialDistribution(1));
          }
        };

        bebParent.start();
        //clients.startAfterTerminationOf(1000, bebParent);
        terminateAfterTerminationOf(3000, bebParent);
      }
    };
    return scen;
  }
}