package cs455.scaling.server;

import cs455.scaling.util.ProjectProperties;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Statistic thread for the server
 */
public class ServerStatisticsThread extends Thread{

  /**
   * All the clients and the number of messages processed for them
   */
  private final HashMap<SelectionKey, Integer> stats = new HashMap<>();
  private final Object statsLock = new Object();


  @Override
  public void run(){

    while(!Thread.currentThread().isInterrupted()){
      printStats(); // print and reset stats
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
  private void runLogic() {
    printStats();
    clearStats();
  }

  /**
   * Clears the stats
   */
  private void clearStats() {

    synchronized (statsLock){
      stats.clear();
    }
  }

  /**
   * Prints the stats
   */
  private void printStats() {

    LinkedList<Integer> deepCopy = getStatsCopy();
    clearStats();

    int total = getTotal(deepCopy); // find the total
    double average = getAverage(total, deepCopy.size()); // find the average
    double deviation = getStdDeviation(average, deepCopy); // find the standard deviation

    System.out.printf("[%s] Server Throughput: %f messages/s, "
            + "Active Client Connections: %d, "
            + "Mean Perclient Throughput: %f messages/s, "
            + "Std. Dev. Of Per-client Throughput: %f messages/s\n",
        new java.util.Date().toString(),
        (double)total/ProjectProperties.STATS_WAIT_TIME_SECONDS,
        deepCopy.size(),
        average/ProjectProperties.STATS_WAIT_TIME_SECONDS,
        deviation/ProjectProperties.STATS_WAIT_TIME_SECONDS);

  }

  private LinkedList<Integer> getStatsCopy() {
    LinkedList<Integer> deepCopy =  new LinkedList<>();
    synchronized (statsLock){
      for (Integer i: stats.values()) {
        int copy = i;
        deepCopy.add(copy);
      }
    }
    return deepCopy;
  }

  /**
   * Find the standard deviation of the stats
   * @param average the mean of all the stats
   * @param deepCopy copy of stats values
   * @return the standard deviation
   */
  private double getStdDeviation(double average, List<Integer> deepCopy) {
    // Size == 0,1 then SD is 0
    if(deepCopy.size() <= 1)
    {
      return 0;
    }

    double sumOfDiff = getSumOfDiffSquared(average, deepCopy);
    return Math.sqrt(sumOfDiff/(deepCopy.size() -1));
  }

  /**
   * Finds the sum of squares of the differences between the messages processed and the mean
   * @param average the mean
   * @param deepCopy copy of stats values
   * @return the sum of differences squared
   */
  private double getSumOfDiffSquared(double average, List<Integer> deepCopy) {
    double sum = 0;
    // Loop through each message count
    for (Integer i: deepCopy) {
      double diff = i - average; // find the diff
      sum += Math.pow(diff,2); // square the diff
    }
    return sum;
  }

  /**
   * Find the average of the messages sent
   * @param total the total of all the messages sent
   * @param size size of deep copy
   * @return the average
   */
  private double getAverage(int total, int size) {
    // If no keys in stats then the average is 0
    if(size == 0){
      return 0;
    }
    return total/size;
  }

  /**
   * Gets the total number of messages sent
   * @return the total number of messages sent
   * @param deepCopy copy of stats values
   */
  private int getTotal(List<Integer> deepCopy) {
    int rt = 0;
    for (Integer i: deepCopy) {
      rt += i;
    }
    return rt;
  }


  /**
   * Adds the number of massages processed for a key. If the key is not in the HashMap it
   * adds the key to it
   * @param key the key to increase the message count for
   */
  public void addToCount(SelectionKey key) {
    synchronized (statsLock){
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
  }

  /**
   * Removes a key from the HashMap. Only used when client disconnects
   * @param key the key to remove
   */
  public void removeKey(SelectionKey key) {
    synchronized (statsLock){
      stats.remove(key);
    }
  }

}
