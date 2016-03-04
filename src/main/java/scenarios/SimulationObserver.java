package scenarios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.Timer;

public class SimulationObserver extends ComponentDefinition{

  private static final Logger logger = LoggerFactory.getLogger(SimulationObserver.class);
  Positive<Timer> timer = requires(Timer.class);
  Positive<Network> net = requires(Network.class);

  public SimulationObserver(Init init) {
    subscribe(startHandler, control);
  }

  Handler<Start> startHandler = new Handler<Start>() {
    @Override
    public void handle(Start start) {
      check_result();
    }
  };

  private void check_result() {
    logger.info("started...");
    GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
    logger.info("execution log:\n{}",
            gv.getValue("simulation.log", StringBuilder.class));
    //gv.terminate();
  }

  public static class Init extends se.sics.kompics.Init<SimulationObserver> {
    public Init() {

    }
  }

}
