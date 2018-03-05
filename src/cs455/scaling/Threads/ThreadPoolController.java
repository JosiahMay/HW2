package cs455.scaling.Threads;

import cs455.scaling.Tasks.Task;
import cs455.scaling.Containers.SynchronizedQueue;


/**
 * The controller for the thread pool and task queue.
 * After the constructor is called, ThreadPoolController.setupThreadPool() must be called to setup
 * the thread pool. After this call ThreadPoolController.startController() to start processing
 * events. This should be its own thread but this in not allowed by the assignment
 */
public class ThreadPoolController extends Thread{

  /**
   * Lock for the thread pool
   */
  private final Object threadPoolLock = new Object();
  /**
   * Lock for the task queue
   */
  private final Object tasksLock = new Object();

  /**
   * A queue for all the worker threads when they are available for work
   */
  private final SynchronizedQueue<WorkerThreads> threadPool;
  /**
   * A FIFO queue for all the tasks that need to be done
   */
  private final SynchronizedQueue<Task> tasks;
  /**
   * The max number of threads to make
   */
  private final int threadPoolSize;
  private boolean threadPoolSetup = false;

  /**
   * Initializes the thread pool and task queues and sets the total threads to make
   * @param poolSize The number of threads for the thread pool to make
   */
  public ThreadPoolController(int poolSize){
    this.threadPoolSize = poolSize;
    this.threadPool = new SynchronizedQueue<>();
    this.tasks = new SynchronizedQueue<>();
  }

  /**
   * Creates the number a of worker threads given during construction, starts them, and adds them
   * to the thread pool.
   * @throws IllegalStateException Attempting to setup Controller for a second time
   */
  public synchronized void setupThreadPool() throws IllegalStateException{

    //Check threads have already been made
    if(threadPoolSetup){
      throw new IllegalStateException("The thread pool is already set up");
    }
    //Make and start threads
    for (int i = 0; i < threadPoolSize; i++) {
      WorkerThreads worker = new WorkerThreads("Worker Thread: " + (i+1), this);
      worker.start();
      threadPool.add(worker); // Add to pool
    }
    // Set setup flag to true
    threadPoolSetup = true;
  }

  /**
   * Starts the controller logic this should be its own thread but not for this assignment
   * @throws IllegalStateException Attempting to start controller without setting up the thread pool
   */
  @Override
  public void run() throws IllegalStateException{
    if(!threadPoolSetup){
      throw new IllegalStateException("The thread pool is not set up");
    }

    while(!Thread.currentThread().isInterrupted()) {
      try {
        Task task = getTask(); // Get a task blocks until there is a task to run
        assignTask(task); // Give a task to a workerThread blocks until thread is available
      } catch (InterruptedException e) {
        System.out.println("Thread pool shutting down worker threads.");
      }
    }
    stopAllThreads();
  }

  /**
   * Loops through the available worker threads an shuts them down
   * Need to find way to stop all threads not just the unused ones
   */
  private void stopAllThreads() {
    synchronized (threadPoolLock){
      while(threadPool.size() != 0){
        threadPool.remove().interrupt();
      }
    }
  }

  /**
   * Adds a task to the queue and wakes up thread waiting for work
   * @param task Task to run
   */
  public synchronized void addTask(Task task){
    tasks.add(task);
    this.notify(); // wake up thread
  }

  /**
   * Gives a workerThread a task to run. Blocks until thread is available
   * @param task Task to for worker thread to run
   * @throws InterruptedException Thread interrupted while waiting
   */
  private synchronized void assignTask(Task task) throws InterruptedException {
    synchronized (threadPoolLock){
      // Wait for available worker thread
      while(threadPool.size() == 0){
        this.wait();
      }
      //System.out.println("Starting Task");
      // Remove the worker thread from the thread pool and set its task
      threadPool.remove().setTask(task);
    }
  }

  /**
   * Gets a task from the task queue. Blocks until there is a task to complete
   * @return A task from the task queue
   * @throws InterruptedException Thread interrupted while waiting
   */
  private synchronized Task getTask() throws InterruptedException {
    synchronized (tasksLock) {
      //System.out.println("Getting Task");
      while (tasks.size() == 0){
        this.wait();
      }
      return tasks.remove();
    }
  }

  /**
   * Adds a worker thread back to the thread pool and wakes up thread
   * waiting for free worker threads
   * @param workerThreads Thread to add back to the pool
   */
  public synchronized void returnThreadToPool(WorkerThreads workerThreads) {
    threadPool.add(workerThreads);
    this.notify(); // Wake up waiting threads
  }

}
