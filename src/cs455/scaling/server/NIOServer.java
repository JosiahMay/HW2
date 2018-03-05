package cs455.scaling.server;

import cs455.scaling.Tasks.Task;
import cs455.scaling.Threads.ThreadPoolController;
import cs455.scaling.util.ProjectProperties;
import cs455.scaling.util.RandomByteAndHashCode;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOServer extends Thread {

  private Selector selector;
  private final int serverPort;

  private final ThreadPoolController threadPool;
  private final ServerStatisticsThread serverStats;

  public NIOServer(int portNumber, ThreadPoolController threadPool, ServerStatisticsThread serverStats){
    this.serverPort = portNumber;
    this.threadPool = threadPool;
    this.serverStats = serverStats;
  }


  private void accept(SelectionKey key) throws IOException {
    ServerSocketChannel servSocket = (ServerSocketChannel) key.channel();
    SocketChannel channel = servSocket.accept();
    System.out.println("Accepting incoming connection ");
    channel.configureBlocking(false);
    channel.register(selector, SelectionKey.OP_READ);
  }



  @Override
  public void run(){
    System.out.println("Starting server");
    try {
      setupServer();

      while (!Thread.currentThread().isInterrupted()) {

        this.selector.select();
        Iterator keys = this.selector.selectedKeys().iterator();
        while (keys.hasNext()) {

          SelectionKey key = (SelectionKey) keys.next();
          keys.remove();

          chooseAction(key);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }



  }

  private void chooseAction(SelectionKey key) throws IOException {
    if (key.isAcceptable ()) {
      this.accept(key);
    }

    if(key.isReadable()){
      Task newTask = new Task(key, serverStats);
      threadPool.addTask(newTask);
    }
  }

  private void setupServer() throws IOException {
    String serverAddress = InetAddress.getLocalHost().getHostAddress();
    selector = Selector.open();
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.socket().bind(new InetSocketAddress(serverAddress, serverPort));
    System.out.println("Starting ServerThreadTest");
    System.out.println(serverAddress + ":" + serverPort);
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
  }
}
