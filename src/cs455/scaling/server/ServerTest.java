package cs455.scaling.server;

import cs455.scaling.Threads.ThreadPoolController;

public class ServerTest {

  public void run(){

    ThreadPoolController threadPool = new ThreadPoolController(10);
    threadPool.setupThreadPool();
    threadPool.start();

    ServerStatisticsThread stats = new ServerStatisticsThread();
    stats.start();

    NIOServer server = new NIOServer(12345,threadPool, stats);

    server.start();

  }

  public static void main(String[] args) {

    ServerTest s = new ServerTest();
    s.run();
  }

}
