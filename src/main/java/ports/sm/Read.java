package ports.sm;

public class Read extends Command{


  public Read (int key) {
    super(key);
  }

  public String toString() {
    return "read " + key;
  }
}
