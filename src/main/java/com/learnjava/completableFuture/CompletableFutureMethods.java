package com.learnjava.completableFuture;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.startTimer;
import static com.learnjava.util.CommonUtil.timeTaken;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.util.CommonUtil;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureMethods {

  public static void main(String[] args) {
    CompletableFutureMethods cfMethod = new CompletableFutureMethods();
//    cfMethod.runAsyncMethod();
//    cfMethod.allOfMethod();
    cfMethod.anyOf();
  }

  /**
   * supplyAsync() expects to return some value
   * For async operation that does not return anything, use runAsync()
   * It takes a runnable instance
   */
  public void runAsyncMethod() {
    CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> hello());
    cf.join();
  }

  /**
   * When you want to combine independent completable futures
   */
  public void allOfMethod() {

    CompletableFuture<String> testMethod1Cf = CompletableFuture.supplyAsync(() -> testMethod1());
    CompletableFuture<String> testMethod2Cf = CompletableFuture.supplyAsync(() -> testMethod2());

    CompletableFuture<Void> allCfs = CompletableFuture.allOf(testMethod1Cf, testMethod2Cf);

    CommonUtil.startTimer();

    /**
     * get() method blocks the thread execution to wait for the completion
     * Also, provide checked exception to handle
     * If you don't use get(), then you can use join() along with exception handling logic(ex exceptionally())
     */
    try {
      allCfs.get();
    } catch (InterruptedException | ExecutionException e) {
      log("exception: " + e);
    }

    String meth1 = testMethod1Cf.join();
    String meth2 = testMethod2Cf.join();
    System.out.println("meth1:" + meth1);
    System.out.println("meth2:" + meth2);

    timeTaken();
  }

  /**
   * If you have multiple data sources, and time varies in each of them
   * It fetches output which gives the fastest result
   *
   */
  public void anyOf() {

    startTimer();
    CompletableFuture<String> db = CompletableFuture.supplyAsync(() -> fromDb());
    CompletableFuture<String> api = CompletableFuture.supplyAsync(() -> fromApi());
    CompletableFuture<String> cache = CompletableFuture.supplyAsync(() -> fromCache());

    CompletableFuture<Object> cfObject = CompletableFuture.anyOf(db, api, cache);

    String data = cfObject.thenApply(result -> result.toString()).join();
    System.out.println(data);

    timeTaken();
  }

  public String fromDb() {
    delay(4000);
    log("from db");
    return "same data";
  }
  public String fromApi() {
    delay(3000);
    log("from api");
    return "same data";
  }
  public String fromCache() {
    delay(2000);
    log("from cache");
    return "same data";
  }

  public String testMethod1() {
    delay(2000);
    return "testMethod1";
  }
  public String testMethod2() {
    delay(3000);
    return "testMethod2";
  }
  public String testMethod3() {
    delay(4000);
    return "testMethod3";
  }

  public void hello() {
    System.out.println("simple method which does not return anything");
  }
}
