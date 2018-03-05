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
  ClientSendBytes(int messageRate, ClientConnectionController controller, SocketChannel channel){
    this.messageRate = messageRate;
    this.controller = controller;
    this.channel = channel;
  }

  @Override
  public void run(){


    try {
    while(!Thread.currentThread().isInterrupted()){
      // Make random bytes
      byte[] bytes = getRandomBytes();
      ByteBuffer buffer = ByteBuffer.wrap(bytes);
      printBytesSent(bytes);

      controller.sendMessage(bytes); // add to sent message list
        channel.write(buffer); // send message
        Thread.sleep(1000/messageRate);//Sleep for the required time

      }

    }catch (IOException e) {
      errorWhenSendingBytes(e);
    } catch (InterruptedException e) {
      System.out.println("Client message sender ending");
    }
  }

  private void printBytesSent(byte[] bytes) {
    if(ProjectProperties.DEBUG_FULL) {
      System.out.println("Sending out: " + RandomByteAndHashCode.SHA1FromBytes(bytes));
    }
  }

  private void errorWhenSendingBytes(IOException e) {
    System.err.println("Error when sending bytes to server");
    if(ProjectProperties.DEBUG_FULL) {e.printStackTrace();}
    controller.interrupt();
  }


  /**
   * Returns a random byte[] of that when hashed will return a size 40 length string
   *
   * @return the random bytes
   */
  private byte[] getRandomBytes() {
    byte[] bytes = RandomByteAndHashCode.randomBytes();
    // Check if string will be 40 in length
    while(RandomByteAndHashCode.SHA1FromBytes(bytes).length() != 40){
      bytes = RandomByteAndHashCode.randomBytes();
    }
    return bytes;
  }


}
