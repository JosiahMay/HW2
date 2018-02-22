package cs455.Threads;

import cs455.Tasks.Task;

public class TestThread extends Thread {

  private Task message;

  TestThread(String s) {
    super(s);
  }

  @Override
  public void run(){
    synchronized (this){
      try {
        while (!Thread.currentThread().isInterrupted()) {

          if(message == null){
            System.out.println(Thread.currentThread().getName() + " has no task");
            wait();
          }

          message.startTask();
          message = null;


        }
      } catch (InterruptedException e) {
        //
      } finally {
        System.out.println(Thread.currentThread().getName() + " stopping");
      }
    }

  }


  void startTask(Task task) {
    synchronized (this) {
      message = task;
      this.notify();
    }
  }

}
