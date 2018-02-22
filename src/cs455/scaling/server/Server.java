package cs455.scaling.server;

import cs455.scaling.util.CommandLineParser;
import cs455.scaling.util.CommandLineParser.Program;
import cs455.scaling.util.ServerArgs;


public class Server {


  public static void main(String[] args) {

    ServerArgs serverArgs = (ServerArgs) CommandLineParser.verifyArgs(args, Program.Server);

    if (serverArgs == null){
      System.exit(1);
    }
    System.out.println("Port Number" + serverArgs.portNumber + " Thread Pool: "
        + serverArgs.threadPoolSize);

  }




}
