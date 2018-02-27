package cs455.scaling.client;

import cs455.scaling.util.ProjectProperties;
import cs455.scaling.util.RandomByteAndHashCode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Sends a random byte[] to a server at a given message rate
 */
public class ClientSendBytes extends Thread{

  /**
   * The sleep time between sending messages it is 1000/messageRate in milliseconds
   */
  private final int messageRate;
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
   * @param messageRate the wait time for 1000/messageRate in milliseconds
   * @param controller The controller of this thread
   * @param channel The channel to send messages to
   */
  public ClientSendBytes(int messageRate,
      ClientConnectionController controller, SocketChannel channel){
    this.messageRate = messageRate;
    this.controller = controller;
    this.channel = channel;
  }

  @Override
  public void run(){

    while(!Thread.currentThread().isInterrupted()){
      // Make random bytes
      byte[] bytes = RandomByteAndHashCode.randomBytes();
      ByteBuffer buffer = ByteBuffer.wrap(bytes);

      try {
        if(ProjectProperties.DEBUG) {
          System.out.println("Sending out: " + RandomByteAndHashCode.SHA1FromBytes(bytes));
        }

        channel.write(buffer); // send message
        controller.sendMessage(bytes); // add to sent message list
      } catch (IOException e) {
        System.err.println("Error when sending bytes to server");
        if(ProjectProperties.DEBUG) {e.printStackTrace();}
        break;
      }

      try {
        //Sleep for the required time
        Thread.sleep(1000/messageRate);
      } catch (InterruptedException e) {
        System.out.println("Client message sender ending");
      }
    }
  }

}
