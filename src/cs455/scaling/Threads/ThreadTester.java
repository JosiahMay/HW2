package cs455.scaling.Threads;

import cs455.scaling.Tasks.ExtendTaskTest;
import cs455.scaling.Tasks.FloodPoolTask;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadTester {

  private void run(){


    ThreadPoolController controller = new ThreadPoolController(10);

    controller.setupThreadPool();
    controller.addTask(new FloodPoolTask(controller));
    controller.startController();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  private void stopThreads(ArrayList<TestThread> threads) {
    for (Thread t : threads) {
      t.interrupt();
    }
  }

  private void sendMessages(ArrayList<TestThread> threads) throws InterruptedException {
    for (int i = 0; i < 1000; i++) {
      TestThread thread = threads.get(ThreadLocalRandom.current().nextInt(threads.size()));
      thread.startTask(new ExtendTaskTest(thread.getName() + " issued message " + i));
      Thread.sleep(10);
    }
  }

  public static void main(String[] args) {

    ThreadTester tester = new ThreadTester();
    tester.run();
  }

}

