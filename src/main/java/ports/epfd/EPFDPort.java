package ports.epfd;

import se.sics.kompics.PortType;

public class EPFDPort extends PortType{

  {
    indication(Restore.class);
    indication(Suspect.class);
  }

}