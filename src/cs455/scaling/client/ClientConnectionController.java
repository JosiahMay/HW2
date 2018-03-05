package cs455.scaling.client;

import cs455.scaling.Containers.SynchronizedSet;
import cs455.scaling.util.ProjectProperties;
import cs455.scaling.util.RandomByteAndHashCode;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


public class ClientConnectionController extends Thread {

  private final String hostAddress;
  private final int hostPort;
  private final int messageRate;
  private final SynchronizedSet<String> bytesSent;
  private SocketChannel channel;
  private final ClientStatisticsThread stats;

  ClientConnectionController(String hostAddress, int hostPort, int messageRate){
    this.hostAddress = hostAddress;
    this.hostPort = hostPort;
    this.messageRate = messageRate;
    this.bytesSent = new SynchronizedSet<>();
    this.stats = new ClientStatisticsThread();
  }

  @Override
  public void run() {

    try {
      channel = setupConnection();
    } catch (IOException e) {
      System.err.println("Could not connect to server " + hostAddress + ":" + hostPort);
      if (ProjectProperties.DEBUG) {
        e.printStackTrace();
      }
      return;
    }

    stats.start();
    ClientSendBytes sender = new ClientSendBytes(messageRate, this, channel);
    sender.start();
    ClientReadBytes reader = new ClientReadBytes(this, channel);
    reader.start();

    while (!Thread.currentThread().isInterrupted()) {

    }
  }




  void sendMessage(byte[] bytes) {
    String hexOfBytes = RandomByteAndHashCode.SHA1FromBytes(bytes);

    if(ProjectProperties.DEBUG){
      System.out.println("Sending bytes with hex code <" + hexOfBytes + "> to server");
    }

    if(bytesSent.add(hexOfBytes)){
      stats.messageSent();
    } else {
      System.err.println("Error in adding <" + hexOfBytes +"> to list of bytes sent");
    }

  }

  void receivedMessage(String bytesRead) {
    if(bytesSent.remove(bytesRead)){
      stats.messageReceived();
    } else {
      System.err.println("Hex values <" + bytesRead + "> not in list of bytes sent");

      if(ProjectProperties.DEBUG){
        System.out.println("Stored HEXs: " + bytesSent.size() );
        bytesSent.printContents();
        System.exit(1);
      }

    }
  }


  private SocketChannel setupConnection() throws IOException {
    SocketChannel channel = SocketChannel.open();
    channel.configureBlocking(false);
    Selector selector = Selector.open();
    channel.register(selector, SelectionKey.OP_CONNECT);
    channel.connect(new InetSocketAddress(this.hostAddress, this.hostPort));

    selector.select();
    channel.finishConnect();
    return channel;

  }


}
