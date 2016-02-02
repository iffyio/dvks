package ports.epfd;

import se.sics.kompics.PortType;

public class EPFLPort extends PortType{

  {
    indication(Restore.class);
    indication(Suspect.class);
  }

}