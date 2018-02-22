package cs455.Tasks;

public class ExtendTaskTest implements Task {

  private final String message;

  public ExtendTaskTest(String message){
    this.message = message;
  }

  @Override
  public synchronized void startTask() {
    System.out.println(message);
  }
}
