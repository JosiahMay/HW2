package cs455.scaling.Tasks;

import cs455.scaling.server.ServerStatisticsThread;
import cs455.scaling.util.ProjectProperties;
import cs455.scaling.util.RandomByteAndHashCode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Task implements Runnable{

  private final SelectionKey key;
  private final SocketChannel channel;
  private final ServerStatisticsThread stats;

  public Task(SelectionKey key, ServerStatisticsThread stats){
    this.key = key;
    this.stats = stats;
    this.channel = (SocketChannel) key.channel();
    key.interestOps(SelectionKey.OP_WRITE);
  }

  @Override
  public void run() {
    try {
      if(read())
      {
        stats.addToCount(key);
      }
    } catch (IOException e) {
      closeAndCancel();
    }
  }

  private void closeAndCancel() {
    try {
      key.cancel();
      channel.close();
    } catch (IOException e) {
      System.err.println("Error while trying to close Task channel");
    }
  }

  private void write( byte[] data) throws IOException {
    ByteBuffer buffer = ByteBuffer.wrap(data);
    channel.write(buffer);
    if(ProjectProperties.DEBUG){
      System.out.println("Data size: " + data.length);
    }

    key.interestOps(SelectionKey.OP_READ);

  }

  private boolean read() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(ProjectProperties.BYTE_BUFFER_SIZE);
    int read = 0;
    while (buffer.hasRemaining() && read != -1) {
      read = channel.read(buffer);
    }

    if (read == -1) {
      stats.removeKey(key);
      /* Connection was terminated by the client. */
      closeAndCancel();
      return false;
    }

    String rt = RandomByteAndHashCode.SHA1FromBytes(buffer.array());
    if(ProjectProperties.DEBUG){
      System.out.println("Read: " + rt );
    }
    write(rt.getBytes());

    return true;
  }


}
