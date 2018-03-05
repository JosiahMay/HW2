package cs455.scaling.client;

import cs455.scaling.Containers.SynchronizedSet;
import cs455.scaling.util.ProjectProperties;
import cs455.scaling.util.RandomByteAndHashCode;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


/**
 * Connects to the server and starts up the sender and receiver threads for the client
 */
public class ClientConnectionController extends Thread {

  /**
   * The server IP address
   */
  private final String hostAddress;
  /**
   * The server port number
   */
  private final int hostPort;
  /**
   * The wait time between messages the large the less the wait time is
   */
  private final int messageRate;
  /**
   * A list of all the hex codes of all the bytes sent
   */
  private final SynchronizedSet<String> bytesSent;
  /**
   * The channel connected to the server
   */
  private SocketChannel channel;
  /**
   * The stats collector for the client
   */
  private final ClientStatisticsThread stats;

  /**
   * Default constructor
   * @param hostAddress server IP address
   * @param hostPort server port number
   * @param messageRate message rate
   */
  ClientConnectionController(String hostAddress, int hostPort, int messageRate){
    this.hostAddress = hostAddress;
    this.hostPort = hostPort;
    this.messageRate = messageRate;

    this.bytesSent = new SynchronizedSet<>();
    this.stats = new ClientStatisticsThread();
  }

  @Override
  public void run() {
    // Setup connection to server
    try {
      channel = setupConnection();
    } catch (IOException e) {
      // Could not connect
      System.err.println("Could not connect to server " + hostAddress + ":" + hostPort);
      if (ProjectProperties.DEBUG_FULL) {
        e.printStackTrace();
      }
      return;
    }

    stats.start();

    // Start send and receive threads
    ClientSendBytes sender = new ClientSendBytes(messageRate, this, channel);
    sender.start();
    ClientReadBytes reader = new ClientReadBytes(this, channel);
    reader.start();

    while (!Thread.currentThread().isInterrupted()) {
      // Wait around
    }
  }

  /**
   * Adds the sent bytes to a list of all bytes sent and updates the stats
   * @param bytes the bytes sent
   */
  void sentMessage(byte[] bytes) {
    // convert bytes to string
    String hexOfBytes = RandomByteAndHashCode.SHA1FromBytes(bytes);

    if(ProjectProperties.DEBUG_FULL){
      System.out.println("Sending bytes with hex code <" + hexOfBytes + "> to server");
    }

    // Add bytes hash to list
    if(bytesSent.add(hexOfBytes)){
      stats.messageSent();
    } else {
      // Should not happen
      System.err.println("Error in adding <" + hexOfBytes +"> to list of bytes sent");
    }
  }

  /**
   * Reads a received message and checks if it is in the list of bytes sent
   * @param bytesRead the hash of the bytes received
   */
  void receivedMessage(String bytesRead) {
    if(bytesSent.remove(bytesRead)){
      // The hash is valid
      stats.messageReceived();
    } else {
      // The hash is invalid
      System.err.println("Hex values <" + bytesRead + "> not in list of bytes sent");
      // Quit if in this debug mode
      if(ProjectProperties.DEBUG_HEX_NOT_FOUND){
        System.out.println("Stored HEXs: " + bytesSent.size() );
        bytesSent.printContents();
        System.exit(1);
      }
    }
  }


  /**
   * Sets up the server connection
   * @return The SocketChannel to the server
   * @throws IOException Could not connect to server
   */
  private SocketChannel setupConnection() throws IOException {

    SocketChannel channel = SocketChannel.open();// Open channel
    channel.configureBlocking(false); //In non blocking mode
    Selector selector = Selector.open();

    channel.register(selector, SelectionKey.OP_CONNECT);
    channel.connect(new InetSocketAddress(this.hostAddress, this.hostPort));

    // Connect to server
    selector.select();
    channel.finishConnect();
    return channel;

  }


}
