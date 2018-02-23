package cs455.Threads;

import cs455.Tasks.Task;
import cs455.scaling.util.SynchronizedQueue;


public class ThreadPoolController {

  private final Object threadPoolLock = new Object();
  private final Object tasksLock = new Object();

  private final SynchronizedQueue<WorkerThreads> threadPool;
  private final SynchronizedQueue<Task> tasks;
  private final int threadPoolSize;

  public ThreadPoolController(int poolSize){
    this.threadPoolSize = poolSize;
    this.threadPool = new SynchronizedQueue<>();
    this.tasks = new SynchronizedQueue<>();
  }

  public synchronized void setupThreadPool(){

    if(threadPool.size() != 0){
      throw new IllegalStateException("The thread pool is already set up");
    }

    for (int i = 0; i < threadPoolSize; i++) {
      WorkerThreads worker = new WorkerThreads("Worker Thread: " + (i+1), this);
      worker.start();
      threadPool.add(worker);
    }
  }

  public void addTask(Task task){
    tasks.add(task);
    this.notify();
  }

  public void startController(){
    while(true){
      try {
        Task task = getTask();
        assignTask(task);
      } catch (InterruptedException e) {
        break;
      } finally {
        System.out.println("Thread pool shutting down worker threads.");
        stopAllThreads();
      }

    }
  }

  private void stopAllThreads() {
    synchronized (threadPoolLock){
      while(threadPool.size() != 0){
        threadPool.remove().interrupt();
      }
    }
  }

  private void assignTask(Task task) throws InterruptedException {
    synchronized (threadPoolLock){
      while(threadPool.size() == 0){
        this.wait();
      }
      threadPool.remove().setTask(task);
    }
  }

  private Task getTask() throws InterruptedException {
    synchronized (tasksLock) {
      while (tasks.size() == 0){
        this.wait();
      }
      return tasks.remove();
    }

  }

  void returnThreadToPool(WorkerThreads workerThreads) {
    threadPool.add(workerThreads);
    this.notify();
  }

}
