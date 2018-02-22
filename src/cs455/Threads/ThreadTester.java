package cs455.Threads;

import cs455.Tasks.ExtendTaskTest;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadTester {

  private void run(){
    ArrayList<TestThread> threads = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      TestThread thread = new TestThread("Thread: " + i);
      thread.start();
      threads.add(thread);
    }

    System.out.println("finished threads");



    try {
      synchronized (this) {
        sendMessages(threads);
        wait(100);
        stopThreads(threads);
      }
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
      wait(10);
    }
  }

  public static void main(String[] args) {

    ThreadTester tester = new ThreadTester();
    tester.run();
  }

}

