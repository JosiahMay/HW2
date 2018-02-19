package cs455.scaling.util;

import javax.print.attribute.standard.NumberUp;

public class CommandLineParser {

  public enum Program {Server, Client}

  public static ArgsReturned verifyArgs(String[] args, Program type){

    switch (type){
      case Client:
        return getClientArgs(args);
      case Server:
        return getSeverArgs(args);
    }

    return null;
  }

  private static ArgsReturned getSeverArgs(String[] args) {

    if (args.length != 2) {
      System.err.println("Invalid number of arguments: " + args.length + " expected 2");
      System.err.println("java cs455.scaling.server.Server portnum thread-pool-size");
      System.exit(1);
    }

    int portNumber = -1;
    int threadPool = -1;
    try {
      portNumber = validInt(args[0]);
    } catch (IllegalArgumentException e){
      System.out.println("The port number argument is invalid\n" + e.getMessage());
      System.exit(1);
    }

    try {
      threadPool = validInt(args[1]);
    } catch (IllegalArgumentException e){
      System.out.println("The thread pool number argument is invalid\n" + e.getMessage());
      System.exit(1);
    }

    return new ServerArgs(portNumber, threadPool);
  }

  private static ArgsReturned getClientArgs(String[] args) {

    if (args.length != 3) {
      System.err.println("Invalid number of arguments: " + args.length + " expected 3");
      System.err.println("java cs455.scaling.client.Client server-host server-port message-rate");
      System.exit(1);
    }

    String hostIp = args[0];
    int hostPort = -1;
    int mesageRate = -1;

    try {
      hostPort = validInt(args[1]);
    } catch (IllegalArgumentException e){
      System.out.println("The host port number argument is invalid\n" + e.getMessage());
      System.exit(1);
    }

    try {
      mesageRate = validInt(args[2]);
    } catch (IllegalArgumentException e){
      System.out.println("The message rate number argument is invalid\n" + e.getMessage());
      System.exit(1);
    }

    return new ClientArgs(hostIp, hostPort, mesageRate);
  }

  private static int validInt(String number){
    int rt;
    try {
      rt = Integer.parseInt(number);
    } catch (NumberFormatException e){
      throw new IllegalArgumentException("The argument is not a number.");
    }
    if(rt <= 0){
      throw new IllegalArgumentException("The number is less then 0");
    }
    return rt;
  }

}
