package cs455.scaling.client;

import cs455.scaling.util.ClientArgs;
import cs455.scaling.util.CommandLineParser;
import cs455.scaling.util.CommandLineParser.Program;


/**
 * Driver class for the client
 */
class Client {


  /**
   * Starts a client
   * @param args  0 - Server in IP or domain name
   *              1 - Server port int
   *              2 - message rate int
   */
  public static void main(String[] args) {

    // Check command line args
    ClientArgs clientArgs = (ClientArgs) CommandLineParser.verifyArgs(args, Program.Client);

    if(clientArgs == null){
      System.exit(1);
    }

    // Print out args
    System.out.println("Server: " + clientArgs.hostIpAddress + ":"
        + clientArgs.hostPortNumber + "\nMessage Rate: "  + clientArgs.messageRate);

    // Start client connections
    ClientConnectionController controller =
        new ClientConnectionController(
            clientArgs.hostIpAddress,
            clientArgs.hostPortNumber,
            clientArgs.messageRate);

    controller.start();
  }

}
