package cs455.scaling.Tasks;

import cs455.scaling.server.ServerStatisticsThread;
import cs455.scaling.util.ProjectProperties;
import cs455.scaling.util.RandomByteAndHashCode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Reads a byte[] from a socket and writes the hash of the byte[] to the socket
 */
public class Task implements Runnable{

  /**
   * Key of what socket being processed
   */
  private final SelectionKey key;
  /**
   * The channel to read
   */
  private final SocketChannel channel;
  /**
   * The stats thread
   */
  private final ServerStatisticsThread stats;

  /**
   * Basic Constructor. It sets the keys interest to OP_WRITE to stop duplicate tasks
   * @param key the key to process
   * @param stats the stats thread
   */
  public Task(SelectionKey key, ServerStatisticsThread stats){
    this.key = key;
    this.stats = stats;
    this.channel = (SocketChannel) key.channel();
    key.interestOps(SelectionKey.OP_WRITE); // stop duplication
  }

  @Override
  public void run() {
    try {
      read();
      stats.addToCount(key); // add to message count
    } catch (IOException e) {
      closeAndCancel();
    }
  }

  /**
   * Removes the key from the stats, closes the channel, and cancels the key
   */
  private void closeAndCancel() {
    try {
      stats.removeKey(key);
      key.cancel();
      channel.close();
    } catch (IOException e) {
      System.err.println("Error while trying to close Task channel");
    }
  }

  /**
   * Writes a byte[] to the channel
   * @param data byte[] to write
   * @throws IOException error writing to channel
   */
  private void write( byte[] data) throws IOException {
    ByteBuffer buffer = ByteBuffer.wrap(data);

    int write = 0;
    // Keep trying to write until buffer is empty
    while(buffer.hasRemaining() && write != -1)
    {
      write = channel.write(buffer);
    }

    checkIfClosed(write); // check for error

    if(ProjectProperties.DEBUG_FULL){
      System.out.println("Data size: " + data.length);
    }

    key.interestOps(SelectionKey.OP_READ);// make key read for reading again

  }

  /**
   * Reads from the channel
   * @throws IOException error from socket
   */
  private void read() throws IOException {

    ByteBuffer buffer = ByteBuffer.allocate(ProjectProperties.BYTE_BUFFER_SIZE);
    int read = 0;
    // Keep trying to write until all bytes are read
    while (buffer.hasRemaining() && read != -1) {
      read = channel.read(buffer);
    }
    checkIfClosed(read);// check for error

    // convert byte[] to hash string
    String rt = RandomByteAndHashCode.SHA1FromBytes(buffer.array());
    if(ProjectProperties.DEBUG_FULL){
      System.out.println("Read: " + rt );
    }

    write(rt.getBytes()); // write hash string
  }


  /**
   * Check if there was error reading or writing
   * @param i the count of items read or written
   * @throws IOException throws error if i == -1
   */
  private void checkIfClosed(int i) throws IOException {
    if (i == -1) {
      throw new IOException();
    }
  }
}
