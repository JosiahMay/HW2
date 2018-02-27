package cs455.scaling.client;

import cs455.scaling.util.ProjectProperties;
import cs455.scaling.util.RandomByteAndHashCode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientReadBytes extends Thread {

  /**
   * The controller of this thread
   */
  private final ClientConnectionController controller;
  /**
   * The channel to send messages to
   */
  private final SocketChannel channel;

  /**
   * Basic constructor
   * @param controller The controller of this thread
   * @param channel The channel to read messages from
   */
  public ClientReadBytes(ClientConnectionController controller, SocketChannel channel){
    this.controller = controller;
    this.channel = channel;
  }

  @Override
  public void run() {

    while (!Thread.currentThread().isInterrupted()) {


      try {
        ByteBuffer buffer = ByteBuffer.allocate(ProjectProperties.BUFFER_SIZE); // setup buffer
        int read = 0; // check if the channel has closed
        //Read message
        while (buffer.hasRemaining() && read != -1) {
          read = channel.read(buffer);
        }
        // Stop the client if channel has closed
        if (read == -1) {
          channel.close();
          errorEncountered("Connection closed on server side");
          break;
        }
        // Convert bytes into String
        String bytesRead = new String(buffer.array());
        if (ProjectProperties.DEBUG) {
          System.out.println("Client read: " + bytesRead);
        }

        // Sends the returned string to the controller
        controller.receivedMessage(bytesRead);

      } catch (IOException e) {

        errorEncountered("Error when reading bytes from server");
        if(ProjectProperties.DEBUG) {e.printStackTrace();}
        break;
      }


    }

    System.out.println("Client read thread closing down");
  }


  /**
   * Print an error statement and tells the client to stop
   * @param message the error message
   */
  private void errorEncountered(String message){
    System.err.println(message);
    controller.interrupt();
  }
}


