package ports.paxos;


import se.sics.kompics.PortType;

public class PaxosPort extends PortType {

  {
    request(Propose.class);
    indication(Deliver.class);
  }
}