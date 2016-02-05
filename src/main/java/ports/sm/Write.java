package ports.sm;

public class Write extends Command{

  public Write (int key, int value) {
    super(key, value);
  }

  public String toString() {
    return "write " + key + " => " + value;
  }
}

