package scenarios;

import main.Routing;
import msg.TAddress;
import parents.client.ClientParent;
import parents.epfd.EPFDParent;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Start;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.SetupEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.util.GlobalView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ScenarioGen {
  static String addr_prefix = "192.193.0.";
  static HashSet<TAddress> nodes;
  static int port = 10000;
  static final int N = 9;

  static{
    nodes = new HashSet<>();
    for (int i = 0; i < N; i++) {
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
            selfAdr.rank = self;
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
          return new ClientParent.Init(selfAdr, self, nodes);
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
            selfAdr.rank = self;
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

  static Operation setupOp = new Operation<SetupEvent>() {
    public SetupEvent generate() {
      return new SetupEvent() {
        @Override
        public void setupGlobalView(GlobalView gv) {
          gv.setValue("simulation.log", new StringBuilder());
        }
      };
    }
  };

  static Operation startObserverOp = new Operation<StartNodeEvent>() {
    public StartNodeEvent generate() {
      return new StartNodeEvent() {
        TAddress selfAdr;

        {
          try {
            selfAdr = new TAddress(InetAddress.getByName("0.0.0.0"), 0);
          } catch (UnknownHostException e) {
            throw new RuntimeException(e);
          }
        }

        public Map<String, Object> initConfigUpdate() {
          HashMap<String, Object> config = new HashMap<>();
          return config;
        }

        public Address getNodeAddress() {
          return selfAdr;
        }

        public Class getComponentDefinition() {
          return SimulationObserver.class;
        }

        public Init getComponentInit() {
          return new SimulationObserver.Init();
        }

      };
    }
  };


  public static SimulationScenario bebScene() {
    SimulationScenario scen = new SimulationScenario() {
      {
        SimulationScenario.StochasticProcess setup = new SimulationScenario.StochasticProcess() {
          {
            raise(1, setupOp);
          }
        };

        SimulationScenario.StochasticProcess kill_node = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(0));
            raise(1, killNodeOp, new BasicIntSequentialDistribution(3));
          }
        };

        SimulationScenario.StochasticProcess restart_node = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(0));
            raise(1, epfdClientOp, new BasicIntSequentialDistribution(3));
          }
        };

        SimulationScenario.StochasticProcess clients = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(0));
            raise(N, startClientOp, new BasicIntSequentialDistribution(0));
          }
        };

        SimulationScenario.StochasticProcess observer = new SimulationScenario.StochasticProcess() {
          {
            raise(1, startObserverOp);
          }
        };

        SimulationScenario.StochasticProcess epfdclients = new SimulationScenario.StochasticProcess() {
          {
            eventInterArrivalTime(constant(0));
            raise(N, epfdClientOp, new BasicIntSequentialDistribution(0));
          }
        };

        setup.start();
        clients.start();
        //kill_node.startAfterTerminationOf(0, clients);
        //restart_node.startAfterTerminationOf(4000, clients);
        //terminateAfterTerminationOf(3000, restart_node);
        observer.startAfterTerminationOf(3000, clients);
        terminateAfterTerminationOf(3000, observer);
      }
    };
    return scen;
  }
}