package cs455.Threads;

import cs455.Tasks.Task;

/**
 * A worker thread the receives a task from the thread pool and runs the tasks. When the thread has
 * no task to run, it waits.
 *
 */
public class WorkerThreads extends Thread{

  /**
   * Lock to make sure the task is not changed by multiple threads
   */
  private final Object taskLock = new Object();
  /**
   * The task to run since the task is constantly changing. The thread sleeps when task is set
   * to null. After the task is set, the thread is notified and the task is run. When the thread is
   * finished with the task it is set to null again.
   */
  private Task task;
  /**
   * The thread poll controller for the thread to add itself to when finished with a task
   */
  private final ThreadPoolController controller;

  /**
   * Constructor that sets the name and the shared thread pool
   * @param name Name of the thread
   * @param controller The thread pool
   */
  public WorkerThreads(String name, ThreadPoolController controller){
    super(name); // Set name
    this.controller = controller;
    task = null; // Make sure task is set to null
  }

  /**
   * Starts the logic for the thread
   */
  @Override
  public void run(){

    while(!Thread.currentThread().isInterrupted()){
      try {
        checkForWork();

        task.startTask();

        taskFinished();

      } catch (InterruptedException e) {
        break;
      } finally {
        System.out.println(Thread.currentThread().getName() + " stopping");
      }

    }

  }

  /**
   * Clears the current task and puts the thread back into the thread pool
   */
  private void taskFinished() {
    task = null;
    controller.returnThreadToPool(this);
  }

  /**
   * Checks if their is a task and waits if there is no task
   * @throws InterruptedException Thread Interrupted while waiting
   */
  private void checkForWork() throws InterruptedException {
    synchronized (taskLock){
      while (task == null){
        this.wait();
      }
    }
  }

  /**
   * Gives a the WorkerThread a task an notifies the thread it has work to do
   * @param task The task to complete
   * @return Was the task set
   * @throws IllegalStateException Trying to set a task to a thread that already has one
   */
  public synchronized boolean setTask(Task task) throws IllegalStateException{
    synchronized (taskLock){
      if(this.task != null){
        throw new IllegalStateException(this.getName() + ": trying to start "
            + "task when thread already assigned a task");
      }
      this.task = task;
      this.notify();
      return true;
    }
  }

}
