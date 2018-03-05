package cs455.scaling.client;


import cs455.scaling.util.ProjectProperties;

/**
 * Prints out the count of messages sent and received by a client every 20 seconds. This count
 * is reset after every print.
 */
public class ClientStatisticsThread extends Thread{


  private int messagesSent;
  private int messagesReceived;

  /**
   * Basic constructor that sets the counts to zero
   */
  ClientStatisticsThread(){
    this.messagesSent = 0;
    this.messagesReceived = 0;
  }

  @Override
  public void run(){

    resetStats();

    while(!Thread.currentThread().isInterrupted()){
      runLogic();
      try {
        Thread.sleep(ProjectProperties.STATS_WAIT_TIME_MILLISECONDS);
      } catch (InterruptedException e) {
        System.out.println("Statistics finished");
      }
    }
  }

  /**
   * Prints and resets the stats
   */
  private synchronized void runLogic() {
    printStats();
    resetStats();
  }


  /**
   * Prints out the counts of messages sent and received
   */
  private synchronized void printStats() {
    System.out.println("[ " + new java.util.Date() +" ] Total Sent Count: "
        + messagesSent + ", Total Received Count: " + messagesReceived);
  }


  /**
   * Resets the counts of messages sent and received back to zero
   */
  private synchronized void resetStats() {
    messagesReceived = 0;
    messagesSent = 0;
  }

  /**
   * Adds to the count of messages sent
   */
  synchronized void messageSent(){
    messagesSent++;
  }

  /**
   * Adds to the count of messages received
   */
  synchronized void messageReceived(){
    messagesReceived++;
  }
}
