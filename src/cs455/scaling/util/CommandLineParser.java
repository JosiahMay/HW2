package cs455.scaling.util;

import javax.print.attribute.standard.NumberUp;

public class CommandLineParser {

  public enum Program {Server, Client}

  private static final String serverCommandLine = "java cs455.scaling.server.Server "
      + "portnum thread-pool-size";
  private static final String clientCommandLine = "java cs455.scaling.client.Client "
      + "server-host server-port message-rate";


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

    checkArgsLength(args.length, 2);

    int portNumber = validInt(args[0],"The port number argument is invalid\n");
    int threadPool = validInt(args[1], "The thread pool number argument is invalid\n");


    return new ServerArgs(portNumber, threadPool);
  }

  private static void checkArgsLength(int receivedLength, int expectedLength) {
    if (receivedLength != expectedLength) {
      System.err.println("Invalid number of arguments: " + receivedLength
          + " expected " + expectedLength);
      if(expectedLength == 2){
        System.err.println(serverCommandLine);
      }
      if(expectedLength == 3){
        System.err.println(clientCommandLine);
      }
      System.exit(1);
    }
  }

  private static ArgsReturned getClientArgs(String[] args) {

    checkArgsLength(args.length, 3);

    String hostIp = args[0];

    int hostPort = validInt(args[1], "The host port number argument is invalid\n");
    int messageRate = validInt(args[2], "The message rate number argument is invalid\n" );


    return new ClientArgs(hostIp, hostPort, messageRate);
  }

  private static int validInt(String number, String message){
    int rt = -1;
    try {
      rt = Integer.parseInt(number);
      if(rt <= 0){
        invalidArgument(message+ "The number is less then 0");
      }
    } catch (NumberFormatException e){
      invalidArgument(message + "The argument is not a number.");
    }
    return rt;
  }

  private static void invalidArgument(String message){
    System.err.println(message);
    System.exit(1);
  }
}
