package main;

import msg.TAddress;

import java.util.HashSet;

public class Routing {

  public static int get_group(int id) {
    return id % 2 == 0? 0 : 1;
  }

  public static TAddress get_leader(int msg_key, HashSet<TAddress> nodes) {
    int msg_group = get_group(msg_key);
    TAddress leader = null;
    for(TAddress node : nodes)
      if (node.group == msg_group && (leader == null || node.rank < leader.rank))
        leader = node;
    return leader;
  }
}