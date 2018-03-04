package cs455.scaling.server;

public class ServerTest {

  public void run(){
    ServerThreadTest server = new ServerThreadTest();

    server.start();

  }

  public static void main(String[] args) {

    ServerTest s = new ServerTest();
    s.run();
  }

}
