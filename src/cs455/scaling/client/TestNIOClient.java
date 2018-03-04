package cs455.scaling.client;


public class TestNIOClient {

  private String serverHost = "localhost";
  private int serverPort = 12345;

  public void run(){
    ClientConnectionController clientThread =
        new ClientConnectionController(serverHost,serverPort, 4);

    clientThread.start();

  }

  public static void main(String[] args) {
    TestNIOClient client = new TestNIOClient();
    client.run();
  }

}
