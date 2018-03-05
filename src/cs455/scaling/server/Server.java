package cs455.scaling.server;

import cs455.scaling.Threads.ThreadPoolController;
import cs455.scaling.util.CommandLineParser;
import cs455.scaling.util.CommandLineParser.Program;
import cs455.scaling.util.ServerArgs;


/**
 * Driver for the server
 */
class Server {


  /**
   * Driver for server
   * @param args  0 - port number to start on
   *              1 - thread pool size
   */
  public static void main(String[] args) {

    //Check Args
    ServerArgs serverArgs = (ServerArgs) CommandLineParser.verifyArgs(args, Program.Server);

    if (serverArgs == null){
      System.exit(1);
    }

    //Print args
    System.out.println("Port Number: " + serverArgs.portNumber + " Thread Pool: "
        + serverArgs.threadPoolSize);

    // Start Thread pool
    ThreadPoolController threadPool = new ThreadPoolController(serverArgs.threadPoolSize);
    threadPool.setupThreadPool();
    threadPool.start();

    ServerStatisticsThread stats = new ServerStatisticsThread();
    NIOServer server = new NIOServer(serverArgs.portNumber,threadPool, stats);

    // Start sever and stats threads
    server.start();
    stats.start();

  }




}
