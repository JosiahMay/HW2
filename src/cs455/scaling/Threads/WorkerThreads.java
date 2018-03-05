package cs455.scaling.Threads;

import cs455.scaling.Tasks.Task;
import cs455.scaling.util.ProjectProperties;

/**
 * A worker thread the receives a task from the thread pool and runs the tasks. When the thread has
 * no task to run, it waits.
 *
 */
class WorkerThreads extends Thread{

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
  WorkerThreads(String name, ThreadPoolController controller){
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
        if(ProjectProperties.DEBUG_FULL){
          System.out.println(Thread.currentThread().getName() + " starting task");
        }
        task.run();
        taskFinished();
      } catch (InterruptedException e) {
        System.out.println(Thread.currentThread().getName() + " stopping");
      }
    }
  }

  /**
   * Clears the current task and puts the thread back into the thread pool
   */
  private synchronized void taskFinished() {
    task = null;
    controller.returnThreadToPool(this);
    //System.out.println(Thread.currentThread().getName() + " finished task");
  }

  /**
   * Checks if their is a task and waits if there is no task
   * @throws InterruptedException Thread Interrupted while waiting
   */
  private synchronized void checkForWork() throws InterruptedException {
      while (task == null) {
        this.wait();
      }
  }

  /**
   * Gives a the WorkerThread a task an notifies the thread it has work to do
   * @param task The task to complete
   * @throws IllegalStateException Trying to set a task to a thread that already has one
   */
  synchronized void setTask(Task task) throws IllegalStateException{
    synchronized (taskLock){
      if(this.task != null){
        throw new IllegalStateException(this.getName() + ": trying to start "
            + "task when thread already assigned a task");
      }

      this.task = task;
      this.notify();
    }
  }

}
