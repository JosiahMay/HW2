package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class TestNIOServer extends Thread {

  private Selector selector;
  private final int buffSize = 8*1024;
  private byte[] data = new byte[buffSize];
  private String serverHost = "localhost";
  private int serverPort = 12345;

  private void write(SelectionKey key) throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();
//You have your data stored in ‘data’, (type: byte[])
    ByteBuffer buffer = ByteBuffer.wrap(data);
    channel.write(buffer);
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
    key.interestOps(SelectionKey.OP_WRITE);
  }

  private void accept(SelectionKey key) throws IOException {
    ServerSocketChannel servSocket = (ServerSocketChannel) key.channel();
    SocketChannel channel = servSocket.accept();
    System.out.println("Accepting incoming connection ");
    channel.configureBlocking(false);
    channel.register(selector, SelectionKey.OP_READ);
  }

  private void startServer() throws IOException {
// Create a Selector
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.socket().bind(new InetSocketAddress(serverHost, serverPort));
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    while (true) {
// wait for events
      this.selector.select();
// wake up to work on selected keys
      Iterator keys = this.selector.selectedKeys().iterator();
      while (keys.hasNext()) {
//more housekeeping
        SelectionKey key = (SelectionKey) keys.next();
        if (key.isAcceptable ()) {
          this.accept(key);
        }
/*other cases such as isReadable() and isWriteable() not shown*/
      } }}

  @Override
  public void run() {




  }
}
