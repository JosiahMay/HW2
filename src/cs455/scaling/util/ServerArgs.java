package cs455.scaling.util;

public class ServerArgs extends ArgsReturned {

  public final int portNumber;
  public final int threadPoolSize;

  ServerArgs(int port, int pool) {
    this.portNumber = port;
    this.threadPoolSize = pool;
  }
}
