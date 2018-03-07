package cs455.scaling.server;

import cs455.scaling.Tasks.Task;
import cs455.scaling.Threads.ThreadPoolController;
import cs455.scaling.util.ProjectProperties;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Server that handles the accepting connection and creating tasks
 */
class NIOServer extends Thread {

  /**
   * The selector for handling keys
   */
  private Selector selector;
  /**
   * The port to start server on
   */
  private final int serverPort;
  /**
   * The thread pool for tasks
   */
  private final ThreadPoolController threadPool;
  /**
   * The stats thread
   */
  private final ServerStatisticsThread serverStats;

  /**
   * Basic constructor
   * @param portNumber the port number
   * @param threadPool the thread pool
   * @param serverStats the stats thread
   */
  NIOServer(int portNumber, ThreadPoolController threadPool, ServerStatisticsThread serverStats){
    this.serverPort = portNumber;
    this.threadPool = threadPool;
    this.serverStats = serverStats;
  }


  /**
   * Accepts a connection and sets the key to read
   * @param key the key to accept a connection to
   * @throws IOException error connecting
   */
  private void accept(SelectionKey key) throws IOException {
    // Configure socket
    ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
    SocketChannel channel = serverSocket.accept();

    if(ProjectProperties.DEBUG_HEX_NOT_FOUND) {
      System.out.println("Accepting incoming connection ");
    }

    channel.configureBlocking(false);
    channel.register(selector, SelectionKey.OP_READ);
  }



  @Override
  public void run(){
    try {
      setupServer(); // setup server

      while (!Thread.currentThread().isInterrupted()) {
        this.selector.select(); // get keys that changes
        Iterator keys = this.selector.selectedKeys().iterator();
        // Loop through keys
        while (keys.hasNext()) {

          SelectionKey key = (SelectionKey) keys.next();
          keys.remove();

          chooseAction(key); //choose what to do based on the key
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }



  }

  /**
   * Either accept a connection or setup a task to be read
   * @param key the key to work on
   * @throws IOException Error connecting to socket
   */
  private void chooseAction(SelectionKey key) throws IOException {
    try{
      if (key.isAcceptable ()) {
        this.accept(key);
      }

      if(key.isReadable()){
        Task newTask = new Task(key, serverStats);
        threadPool.addTask(newTask);
      }
    } catch (CancelledKeyException e){
      // If key gets canceled it means the client has disconnected
      if(ProjectProperties.DEBUG_HEX_NOT_FOUND){
        System.err.println("A client has disconnected");
      }


    }

  }

  /**
   * Sets up the server
   * @throws IOException Error connecting to server
   */
  private void setupServer() throws IOException {
    String serverAddress = InetAddress.getLocalHost().getHostAddress();
    selector = Selector.open();
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.socket().bind(new InetSocketAddress(serverAddress, serverPort));
    System.out.println("Starting Server at");
    System.out.println(serverAddress + ":" + serverPort);
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
  }
}
