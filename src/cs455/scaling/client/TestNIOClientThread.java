package cs455.scaling.client;

import cs455.scaling.util.RandomByteAndHashCode;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class TestNIOClientThread extends Thread{

  private final String hostAddress;
  private final int hostPort;
  private final int buffSize = 8*1024;

  public TestNIOClientThread(String hostAddress, int hostPort){
    this.hostAddress = hostAddress;
    this.hostPort = hostPort;
  }

  @Override
  public void run(){


    try {
      SocketChannel channel = SocketChannel.open();
      channel.configureBlocking(false);
      Selector selector = Selector.open();
      channel.register(selector, SelectionKey.OP_CONNECT);
      channel.connect(new InetSocketAddress(this.hostAddress, this.hostPort));

      while (true) {
        selector.select();
        Set<SelectionKey> readyKeys = selector.selectedKeys();

        // process each ready key...
        Iterator<SelectionKey> iterator = readyKeys.iterator();
        while (iterator.hasNext()) {
          SelectionKey key = (SelectionKey) iterator.next();
          iterator.remove();

          //other operations
          if (key.isConnectable()) {
            this.connect(key);
          }

          if(key.isWritable()){
            this.write(key);
            Thread.sleep(100);
          }

          if(key.isReadable()){
            this.read(key);
          }

        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }


  }

  private void connect(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();
    channel.finishConnect();
    key.interestOps(SelectionKey.OP_WRITE);
  }

  private void write(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();
    byte[] bytes = RandomByteAndHashCode.randomBytes();
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    channel.write(buffer);
    System.out.println("Sending out: " + RandomByteAndHashCode.SHA1FromBytes(bytes));
    key.interestOps(SelectionKey.OP_READ);
  }

  private void read(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(buffSize);
    int read = 0;
    try {
      while (buffer.hasRemaining() && read != -1) {
        read = channel.read(buffer);
      }
    } catch (IOException e) {
/* Abnormal termination */
// Cancel the key and close the socket channel
    }
// You may want to flip the buffer here

//continued
    if (read == -1) {
/* Connection was terminated by the client. */
// Cancel the key and close the socket channel
      key.cancel();
      channel.close();
      return;
    }
    System.out.println("Read: " + RandomByteAndHashCode.SHA1FromBytes(buffer.array()));
    key.interestOps(SelectionKey.OP_WRITE);
  }
}
