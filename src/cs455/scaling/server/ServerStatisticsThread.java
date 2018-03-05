package cs455.scaling.server;

import cs455.scaling.util.ProjectProperties;
import java.nio.channels.SelectionKey;
import java.util.HashMap;

public class ServerStatisticsThread extends Thread{

  private final HashMap<SelectionKey, Integer> stats;


  public ServerStatisticsThread(){
    this.stats = new HashMap<>();
  }

  @Override
  public void run(){

    while(!Thread.currentThread().isInterrupted()){
      runLogic();
      try {
        Thread.sleep(ProjectProperties.STATS_WAIT_TIME_MILLISECONDS);
      } catch (InterruptedException e) {
        System.out.println("Server stats thread shutting down");
      }
    }

  }

  private synchronized void runLogic() {
    printStats();
    clearStats();
  }

  private void clearStats() {
    stats.clear();
  }

  private void printStats() {
    int total = getTotal();
    double average = getAverage(total);
    double deviation = getStdDeviation(average);

    System.out.printf("[%s] Server Throughput: %f messages/s, "
            + "Active Client Connections: %d, "
            + "Mean Perclient Throughput: %f messages/s, "
            + "Std. Dev. Of Per-client Throughput: %f messages/s",
        new java.util.Date().toString(),
        (double)total/ProjectProperties.STATS_WAIT_TIME_SECONDS,
        stats.size(),
        average/ProjectProperties.STATS_WAIT_TIME_SECONDS,
        deviation/ProjectProperties.STATS_WAIT_TIME_SECONDS);

  }

  private double getStdDeviation(double average) {
    if(stats.size() <= 1)
    {
      return 0;
    }
    double sumOfDiff = getSumOfDiffSquared(average);
    return Math.sqrt(sumOfDiff/(stats.size() -1));
  }

  private double getSumOfDiffSquared(double average) {
    double sum = 0;
    for (Integer i: stats.values()) {
      double diff = i - average;
      sum += Math.pow(diff,2);
    }
    return sum;
  }

  private double getAverage(int total) {
    if(stats.size() == 0){
      return 0;
    }
    return total/stats.size();
  }

  private int getTotal() {
    int rt = 0;
    for (Integer i: stats.values()) {
      rt += i;
    }
    return rt;
  }


  public synchronized void addToCount(SelectionKey key) {
    if(stats.containsKey(key))
    {
      int newValue = stats.get(key) + 1;
      stats.replace(key, newValue);
    } else {
      stats.put(key, 1);
    }
  }

  public synchronized boolean removeKey(SelectionKey key)
  {
    Integer results = stats.remove(key);
    return results != null;
  }

}
