package cs455.scaling.client;

import cs455.scaling.util.ProjectProperties;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

class ClientReadBytes extends Thread {

  /**
   * The controller of this thread
   */
  private final ClientConnectionController controller;
  /**
   * The channel to send messages to
   */
  private final SocketChannel channel;

  /**
   * Buffer to read the channel
   */
  private final ByteBuffer buffer = ByteBuffer.allocate(ProjectProperties.STRING_BUFFER_SIZE);

  /**
   * Basic constructor
   * @param controller The controller of this thread
   * @param channel The channel to read messages from
   */
  ClientReadBytes(ClientConnectionController controller, SocketChannel channel){
    this.controller = controller;
    this.channel = channel;

  }

  @Override
  public void run() {
    try {
      while (readMessage()) {

        // Convert bytes into String
        String bytesRead = getBytesHexString();

        // Sends the returned string to the controller
        controller.receivedMessage(bytesRead);

        // Clear buffer
        buffer.clear();
      }


    }catch (IOException e) {
      errorEncountered("Error when reading bytes from server");
      if(ProjectProperties.DEBUG_FULL) {e.printStackTrace();}
    }

    System.out.println("Client read thread closing down");
  }

  /**
   * Reads a message from the channel
   * @return If the message was read successfully
   * @throws IOException Error reading channel
   */
  private boolean readMessage() throws IOException {
    int read = 0; // check if the channel has closed
    //Read message
    while (buffer.hasRemaining() && read != -1) {
      read = channel.read(buffer);
    }
    // Stop the client if channel has closed
    if (read == -1) {
      channel.close();
      errorEncountered("Connection closed on server side");
    }
    return read != -1;
  }


  /**
   * Reads a buffer for the hash value
   * @return The string in the byte buffer
   */
  private String getBytesHexString() {
    String bytesRead = new String(buffer.array());
    if (ProjectProperties.DEBUG_FULL) {
      System.out.println("Client read: " + bytesRead);
    }
    return bytesRead;
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


