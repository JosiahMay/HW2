package cs455.scaling.util;


/**
 * Parses the commands given to the program from the command line
 */
public class CommandLineParser {

  /**
   * The type of programs this parser works with
   */
  public enum Program {Server, Client}

  /**
   * The proper command to start a server
   */
  private static final String serverCommandLine = "java cs455.scaling.server.Server "
      + "portnum thread-pool-size";

  /**
   * The proper command to start a client
   */
  private static final String clientCommandLine = "java cs455.scaling.client.Client "
      + "server-host server-port message-rate";


  /**
   * Static method to parse command line arguments
   * Works for client and server
   * @param args The args to parse
   * @param type What program is the commands for
   * @return A class with all the arguments assigned to final
   */
  public static ArgsReturned verifyArgs(String[] args, Program type){

    switch (type){
      case Client:
        return getClientArgs(args);
      case Server:
        return getSeverArgs(args);
      default:
        System.err.println("Invalid program type given");
        System.exit(1);
        return null;
    }
  }

  /**
   * Parses arguments for a server
   * @param args he args to parse
   * @return The args stored in a class
   */
  private static ArgsReturned getSeverArgs(String[] args) {

    checkArgsLength(args.length, 2);

    int portNumber = validInt(args[0],"The port number argument is invalid\n");
    int threadPool = validInt(args[1], "The thread pool number argument is invalid\n");

    return new ServerArgs(portNumber, threadPool);
  }

  /**
   * Parses arguments for a client
   * @param args he args to parse
   * @return The args stored in a class
   */
  private static ArgsReturned getClientArgs(String[] args) {

    checkArgsLength(args.length, 3);

    String hostIp = args[0];
    int hostPort = validInt(args[1], "The host port number argument is invalid\n");
    int messageRate = validInt(args[2], "The message rate number argument is invalid\n" );

    return new ClientArgs(hostIp, hostPort, messageRate);
  }

  /**
   * Checks to see if the number of arguments match the type for the program
   * @param receivedLength The number of arguments received
   * @param expectedLength The number of arguments expected
   */
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

  /**
   * Checks to see if Integer commands are in int format and above zero
   * @param number String to change to int
   * @param message Error message if number is not valid
   * @return The string converted to int
   */
  private static int validInt(String number, String message){
    int rt = -1;
    try {
      rt = Integer.parseInt(number);
      if(rt <= 0){
        // Int is less then zero
        invalidArgument(message+ "The number is less then 0");
      }
    } catch (NumberFormatException e){
      // String does not contain an int
      invalidArgument(message + "The argument is not a number.");
    }
    return rt;
  }

  /**
   * Print error message then exit program
   * @param message The error message
   */
  private static void invalidArgument(String message){
    System.err.println(message);
    System.exit(1);
  }
}
