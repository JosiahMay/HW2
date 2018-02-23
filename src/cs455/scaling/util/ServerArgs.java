package cs455.scaling.util;

/**
 * Wrapper class for the args for starting a server
 */
public class ServerArgs implements ArgsReturned {

  /**
   * The port number you want your server to run on
   */
  public final int portNumber;
  /**
   * The number of threads in your thread pool
   */
  public final int threadPoolSize;

  /**
   * Constructor that sets the port number and thread pool size
   * @param port The port number
   * @param pool The thread pool size
   */
  ServerArgs(int port, int pool) {
    this.portNumber = port;
    this.threadPoolSize = pool;
  }
}
