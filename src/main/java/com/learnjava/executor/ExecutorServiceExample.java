package com.learnjava.executor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceExample {

  public static void main(String[] args) {

    ExecutorService service = Executors.newCachedThreadPool();

    Future<String> future = service.submit(() -> task());
    System.out.println("Main thread:" + Thread.currentThread().getName());
    try {
      String s = future.get();
      System.out.println(s);
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }

    service.shutdown();


  }

  public static String task() {
    try {
      TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println("hello");
    return "Hello world";
  }
}
