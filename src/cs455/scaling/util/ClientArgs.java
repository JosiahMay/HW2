package cs455.scaling.util;

public class ClientArgs extends ArgsReturned{

  public final String hostIpAddress;
  public final int hostPortNumber;
  public final int messageRate;

  ClientArgs(String ip, int port, int rate){
    this.hostIpAddress = ip;
    this.hostPortNumber = port;
    this.messageRate = rate;
  }

}
