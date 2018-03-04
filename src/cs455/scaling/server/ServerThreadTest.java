package cs455.scaling.server;

import cs455.scaling.util.ProjectProperties;
import cs455.scaling.util.RandomByteAndHashCode;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ServerThreadTest extends Thread{

  private Selector selector;
  private final int buffSize = 8*1024;
  private String serverHost = "localhost";
  private int serverPort = 12345;

  private void write(SelectionKey key, byte[] data) throws IOException {
    SocketChannel channel = (SocketChannel) key.channel();
//You have your data stored in ‘data’, (type: byte[])
    ByteBuffer buffer = ByteBuffer.wrap(data);
    channel.write(buffer);
    key.interestOps(SelectionKey.OP_READ);

    if(ProjectProperties.DEBUG){
      System.out.println("Data size: " + data.length);
    }

  }

  private void read(SelectionKey key) throws IOException {
    key.interestOps(SelectionKey.OP_WRITE);
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
    //buffer.flip();
//continued
    if (read == -1) {
/* Connection was terminated by the client. */
// Cancel the key and close the socket channel
      key.cancel();
      channel.close();
      return;
    }
    String rt = RandomByteAndHashCode.SHA1FromBytes(buffer.array());
    if(ProjectProperties.DEBUG){
      System.out.println("Read: " + rt );
    }
    write(key, rt.getBytes());
  }

  private void accept(SelectionKey key) throws IOException {
    ServerSocketChannel servSocket = (ServerSocketChannel) key.channel();
    SocketChannel channel = servSocket.accept();
    System.out.println("Accepting incoming connection ");
    channel.configureBlocking(false);
    channel.register(selector, SelectionKey.OP_READ);
  }

  /*private void startServer() throws IOException {
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
     // } }}


  @Override
  public void run(){
    System.out.println("Starting ServerThreadTest");
    try {
      selector = Selector.open();
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
          keys.remove();

          if (key.isAcceptable ()) {
            this.accept(key);
          }
/*other cases such as isReadable() and isWriteable() not shown*/
          if(key.isReadable()){
            this.read(key);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }



}

}
