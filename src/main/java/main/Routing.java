package main;

public class Routing {

  public static int get_group(int id) {
    return id % 2 == 0? 0 : 1;
  }

}