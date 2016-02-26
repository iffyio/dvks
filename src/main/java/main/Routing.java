package main;

import msg.TAddress;
import ports.paxos.PaxosCommand;

import java.util.HashSet;
import java.util.LinkedList;

public class Routing {

  public static int get_group(int id) {
    //return id % 2 == 0? 0 : 1;
    return id % 3;
  }

  public static TAddress get_leader(int msg_key, HashSet<TAddress> nodes) {
    int msg_group = get_group(msg_key);
    TAddress leader = null;
    for(TAddress node : nodes)
      if (node.group == msg_group && (leader == null || node.rank < leader.rank))
        leader = node;
    return leader;
  }

  public static String vsuf_to_s(LinkedList<PaxosCommand> l) {
    StringBuffer sb = new StringBuffer();
    for (PaxosCommand pc : l)
      sb.append(pc.toString()).append(",");
    if (sb.toString().equals("")) sb.append("empty vsuf");
    return sb.toString();
  }
}