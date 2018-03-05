package cs455.scaling.server;

import cs455.scaling.util.ProjectProperties;
import java.nio.channels.SelectionKey;
import java.util.HashMap;

/**
 * Statistic thread for the server
 */
public class ServerStatisticsThread extends Thread{

  /**
   * All the clients and the number of messages processed for them
   */
  private final HashMap<SelectionKey, Integer> stats = new HashMap<>();


  @Override
  public void run(){

    while(!Thread.currentThread().isInterrupted()){
      runLogic(); // print and reset stats
      try {
        Thread.sleep(ProjectProperties.STATS_WAIT_TIME_MILLISECONDS); // wait to print again
      } catch (InterruptedException e) {
        System.out.println("Server stats thread shutting down");
      }
    }

  }

  /**
   * Prints and resets the stats
   */
  private synchronized void runLogic() {
    printStats();
    clearStats();
  }

  /**
   * Clears the stats
   */
  private void clearStats() {
    stats.clear();
  }

  /**
   * Prints the stats
   */
  private void printStats() {

    int total = getTotal(); // find the total
    double average = getAverage(total); // find the average
    double deviation = getStdDeviation(average); // find the standard deviation

    System.out.printf("[%s] Server Throughput: %f messages/s, "
            + "Active Client Connections: %d, "
            + "Mean Perclient Throughput: %f messages/s, "
            + "Std. Dev. Of Per-client Throughput: %f messages/s\n",
        new java.util.Date().toString(),
        (double)total/ProjectProperties.STATS_WAIT_TIME_SECONDS,
        stats.size(),
        average/ProjectProperties.STATS_WAIT_TIME_SECONDS,
        deviation/ProjectProperties.STATS_WAIT_TIME_SECONDS);

  }

  /**
   * Find the standard deviation of the stats
   * @param average the mean of all the stats
   * @return the standard deviation
   */
  private double getStdDeviation(double average) {
    // Size == 0,1 then SD is 0
    if(stats.size() <= 1)
    {
      return 0;
    }

    double sumOfDiff = getSumOfDiffSquared(average);
    return Math.sqrt(sumOfDiff/(stats.size() -1));
  }

  /**
   * Finds the sum of squares of the differences between the messages processed and the mean
   * @param average the mean
   * @return the sum of differences squared
   */
  private double getSumOfDiffSquared(double average) {
    double sum = 0;
    // Loop through each message count
    for (Integer i: stats.values()) {
      double diff = i - average; // find the diff
      sum += Math.pow(diff,2); // square the diff
    }
    return sum;
  }

  /**
   * Find the average of the messages sent
   * @param total the total of all the messages sent
   * @return the average
   */
  private double getAverage(int total) {
    // If no keys in stats then the average is 0
    if(stats.size() == 0){
      return 0;
    }
    return total/stats.size();
  }

  /**
   * Gets the total number of messages sent
   * @return the total number of messages sent
   */
  private int getTotal() {
    int rt = 0;
    for (Integer i: stats.values()) {
      rt += i;
    }
    return rt;
  }


  /**
   * Adds the number of massages processed for a key. If the key is not in the HashMap it
   * adds the key to it
   * @param key the key to increase the message count for
   */
  public synchronized void addToCount(SelectionKey key) {
    if(stats.containsKey(key))
    {
      // Key in Hash Map
      int newValue = stats.get(key) + 1; // add one to the total
      stats.replace(key, newValue);
    } else {
      // Add key to the hash map
      stats.put(key, 1);
    }
  }

  /**
   * Removes a key from the HashMap. Only used when client disconnects
   * @param key the key to remove
   */
  public synchronized void removeKey(SelectionKey key)
  {
    Integer results = stats.remove(key);
  }

}
