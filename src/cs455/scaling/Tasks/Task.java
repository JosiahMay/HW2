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
      read();
      stats.addToCount(key);
    } catch (IOException e) {
      closeAndCancel();
    }
  }

  private void closeAndCancel() {
    try {
      stats.removeKey(key);
      key.cancel();
      channel.close();
    } catch (IOException e) {
      System.err.println("Error while trying to close Task channel");
    }
  }

  private void write( byte[] data) throws IOException {
    ByteBuffer buffer = ByteBuffer.wrap(data);
    int write = 0;
    while(buffer.hasRemaining() && write != -1)
    {
      write = channel.write(buffer);
    }

    checkIfClosed(write);

    if(ProjectProperties.DEBUG_FULL){
      System.out.println("Data size: " + data.length);
    }

    key.interestOps(SelectionKey.OP_READ);


  }

  private void read() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(ProjectProperties.BYTE_BUFFER_SIZE);
    int read = 0;
    while (buffer.hasRemaining() && read != -1) {
      read = channel.read(buffer);
    }
    checkIfClosed(read);


    String rt = RandomByteAndHashCode.SHA1FromBytes(buffer.array());
    if(ProjectProperties.DEBUG_FULL){
      System.out.println("Read: " + rt );
    }

    write(rt.getBytes());
  }


  private void checkIfClosed(int i) throws IOException {
    if (i == -1) {
      throw new IOException();
    }
  }
}
