package cs455.Threads;

import cs455.Tasks.Task;
import cs455.scaling.util.SynchronizedQueue;

public class ThreadPoolController {


  private final SynchronizedQueue<WorkerThreads> threadPool;
  private final SynchronizedQueue<Task> tasks;
  private final int threadPoolSize;

  public ThreadPoolController(int poolSize){
    this.threadPoolSize = poolSize;
    this.threadPool = new SynchronizedQueue<>();
    this.tasks = new SynchronizedQueue<>();
  }

  void returnThreadToPool(WorkerThreads workerThreads) {
    threadPool.add(workerThreads);
    this.notify();
  }

}
