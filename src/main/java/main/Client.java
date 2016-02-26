package main;

import msg.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.sm.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;


public class Client extends ComponentDefinition {

  private static final Logger logger = LoggerFactory.getLogger(Client.class);
  private final TAddress self;
  private final int id;
  private long delay = 100;
  private UUID timerId;
  private List<Command> commands;
  private int c = 0;
  List<Command> CAS_Commands;
  Random random;

  //Positive<BebPort> beb_port = requires(BebPort.class);
  Positive<Timer> timer = requires(Timer.class);
  //Positive<Network> network = requires(Network.class);
  Positive<SMPort> sm_port = requires(SMPort.class);


  public Client(Init init) {
    this.self = init.self;
    this.id = init.id;
    random = new Random((long)id);

    subscribe(startHandler, control);
    subscribe(commandReturnHandler, sm_port);
    subscribe(timeoutHandler, timer);
  }


  Handler<Start> startHandler = new Handler<Start>() {
    public void handle(Start event) {
      //logger.info("Client started on node {}!", self);
      /*if (id == 3) {
        //trigger(new Command(22, 4), sm_port); //read
        //trigger(new Command(21), sm_port); //read
        trigger(new Command(14), sm_port); //read
      }else if (id == 5) {
        trigger(new Command(21, 4), sm_port); //read
      }*/
      load_commands();
      set_timer();
    }
  };

  Handler<CommandReturn> commandReturnHandler = new Handler<CommandReturn>() {
    @Override
    public void handle(CommandReturn cr) {
      logger.info("{} received {}", self, cr);
      /*if (commandReturn.cmd.op == Op.WRITE) {
        trigger(new Command(21), sm_port); //read
      }else if (k == 0 && commandReturn.cmd.op == Op.READ && commandReturn.cmd.key == 14){
        k++;
        trigger(new Command(14, -1, commandReturn.value), sm_port);
        trigger(new Command(14), sm_port); //read
      }*/
      Command cas_command = null; //potential read reply for cas command
      if (cr.cmd.op == Op.READ && (cas_command = get_cas_command(cr.cmd.key))!= null){
        cas_command.ref = cr.value;
        trigger(cas_command, sm_port);
      }
    }
  };

  private Command get_cas_command(int key) {
    for (Command cmd : CAS_Commands){
      if (cmd.key == key) {
        CAS_Commands.remove(cmd);
        return cmd;
      }
    }
    return null;
  }

  private void execute_command() {
    if (c < commands.size()) {
      Command cmd = commands.get(c++);
      if (cmd.op == Op.CAS) {
        CAS_Commands.add(cmd); //pending cas
        trigger(new Command(cmd.key), sm_port); //retrieve current value first
      }else{
        trigger(cmd, sm_port);
      }
      set_timer();
    }
  }

  private void load_commands() {
    commands = new ArrayList<>();
    CAS_Commands = new ArrayList<>();
    try {
      FileInputStream fis = new FileInputStream("data/"+id+".log");
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));
      String line = null;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split("\\s");
        int l = parts.length;
        if (l != 2 && l != 3) continue;

        String op = parts[0]; int key = Integer.parseInt(parts[1]);

        switch (op) {
          case "READ":
            commands.add(new Command(key));
            continue;
        }
        int val = Integer.parseInt(parts[2]);
        switch (op) {
          case "WRITE":
            commands.add(new Command(key,val));
            continue;
          case "CAS":
            commands.add(new Command(key,val, 0));//dummy ref
        }
      }
      br.close();
      /*logger.info("{} loaded {} commands", self, commands.size());
      for (Command c : commands)
        logger.info("{}", c);
        */
    }catch(Exception e) {
      e.printStackTrace();
      return;
    }
  }

  private void set_timer() {
    delay = random.nextInt((100 - 30) + 1) + 30;
    ScheduleTimeout st = new ScheduleTimeout(delay);
    CommandTimeout timeout = new CommandTimeout(st);
    st.setTimeoutEvent(timeout);
    trigger(st, timer);
    timerId = timeout.getTimeoutId();
  }

  public void tearDown() {
    trigger(new CancelPeriodicTimeout(timerId), timer);
  }

  Handler<CommandTimeout> timeoutHandler = new Handler<CommandTimeout>() {
    @Override
    public void handle(CommandTimeout commandTimeout) {
      execute_command();
    }
  };

  public static class CommandTimeout extends Timeout {
    public CommandTimeout(ScheduleTimeout st) {
      super(st);
    }
  }


  public static class Init extends se.sics.kompics.Init<Client> {
    public final TAddress self;
    public int id;
    public Init(TAddress self, int id) {
      this.self = self;
      this.id = id;
    }
  }

}
