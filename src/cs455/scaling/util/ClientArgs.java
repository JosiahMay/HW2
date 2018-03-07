package cs455.scaling.util;

/**
 * Wrapper class for the args for a client args
 */
public class ClientArgs implements ArgsReturned {

  /**
   * The IP address of the server
   */
  public final String hostIpAddress;
  /**
   * The port number the server is listening on
   */
  public final int hostPortNumber;
  /**
   * How fast messages should be sent to the server
   */
  public final int messageRate;

  /**
   * Constructor that sets the server IP address and port number
   * and the message rate of the client
   * @param ip IP address of the server
   * @param port  port number of the server
   * @param rate message rate for the client
   */
  ClientArgs(String ip, int port, int rate){
    this.hostIpAddress = ip;
    this.hostPortNumber = port;
    this.messageRate = rate;
  }

}
