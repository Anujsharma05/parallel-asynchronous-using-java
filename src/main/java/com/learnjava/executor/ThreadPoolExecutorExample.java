package com.learnjava.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorExample {

  private int counter = 0;

  public static void main(String[] args) {
    ThreadPoolExecutorExample executor = new ThreadPoolExecutorExample();
    executor.execute();
  }

  public void execute() {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 3, 5, TimeUnit.MINUTES,
        new ArrayBlockingQueue<>(2),
        new CustomThreadFactory(),
        new CustomRejectedExecutionHandler());

    for(int i=0; i<6; i++) {
      executor.execute(() -> task());
    }

    executor.shutdown();
  }

  public void task() {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println(String.format("Task: %d, thread: %s" , counter++ ,Thread.currentThread().getName()));
  }

  class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      System.out.println("thread rejected: " + Thread.currentThread().getName());
    }
  }

  class CustomThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
      Thread th = new Thread(r);
      th.setPriority(Thread.NORM_PRIORITY);
      return th;
    }
  }
}
