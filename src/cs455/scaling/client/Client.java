package cs455.scaling.client;

import cs455.scaling.util.ClientArgs;
import cs455.scaling.util.CommandLineParser;
import cs455.scaling.util.CommandLineParser.Program;


public class Client {



  public static void main(String[] args) {

    ClientArgs clientArgs = (ClientArgs) CommandLineParser.verifyArgs(args, Program.Client);


    if(clientArgs == null){
      System.exit(1);
    }
    System.out.println("Server: " + clientArgs.hostIpAddress + ":"
        + clientArgs.hostPortNumber + "\nMessage Rate: "  + clientArgs.messageRate);

    ClientConnectionController controller =
        new ClientConnectionController(
            clientArgs.hostIpAddress,
            clientArgs.hostPortNumber,
            clientArgs.messageRate);

    controller.start();
  }

}
