package main;

import hosts.beb.BebHost;
import hosts.client.ClientHost;
import msg.TAddress;
import parents.client.ClientParent;
import se.sics.kompics.Kompics;

import java.net.InetAddress;
import java.util.HashSet;

public class Main{
  public static void main(String[] args) {
    try {
      InetAddress myip = InetAddress.getLocalHost();
      int port = 12345;
      TAddress self = new TAddress(myip, port);
      self.group = 3;

      HashSet<TAddress> nodes = new HashSet<>();
      nodes.add(self);

      Kompics.createAndStart(ClientHost.class, new ClientHost.Init(self, nodes));
      Thread.sleep(1000);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    Kompics.shutdown();
    System.exit(0);
  }
}
