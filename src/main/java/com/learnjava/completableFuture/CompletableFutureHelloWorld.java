package com.learnjava.completableFuture;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.startTimer;
import static com.learnjava.util.CommonUtil.timeTaken;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.service.HelloWorldService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureHelloWorld {

  private HelloWorldService hws;

  public CompletableFutureHelloWorld(HelloWorldService hws) {
    this.hws = hws;
  }

  public static void main(String[] args) {

    HelloWorldService hws = new HelloWorldService();
    CompletableFutureHelloWorld main = new CompletableFutureHelloWorld(hws);

//    main.completableFuture();
//    main.assignment();
  }

  void assignment() {
    //assignment
    CompletableFuture<String> completableFuture = helloWorld_withSize();
    String output = completableFuture.join();
    System.out.println(output);
  }

  void completableFuture() {
    HelloWorldService hws = new HelloWorldService();

    /**
     * Immediately returns the completableFuture instance
     */
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
        () -> hws.helloWorld());

    /**
     * thenApply(): Function that takes the result and return value after
     * transformation
     *
     * join(): Blocks the execution until the final result is computed
     */
    completableFuture
        .thenApply(result -> result.toUpperCase())
        .thenAccept(result -> log("result: " + result)).join();

    log("done!");
//    delay(2000);
  }

  public CompletableFuture<String> helloWorld_withSize() {
    String input = "HELLO WORLD";
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
        () -> hws.helloWorld_withSize(input));
    return completableFuture;
  }

  /**
   * thenCombine() -> To combine multiple completable futures We can chain multiple thenCombine()
   * <p>
   * Running the method through test cases
   */
  public String helloWorld_combine_async_calls() {
    CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> hws.hello());
    CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> hws.world());

    /**
     * BiFunction is used to combine the result of two completable futures
     */
    return hello.thenCombine(world,
            (resultFromHello, resultFromWorld) -> resultFromHello + resultFromWorld)
        .thenApply(String::toUpperCase).join();
  }

  public String helloWorld_combine_3_async_calls() {

    startTimer();
    CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> hws.hello());
    CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> hws.world());
    CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
      delay(1000);
      return " Hi Completable Future";
    });

    String result = hello
        .thenCombine(world,
            (resultFromHello, resultFromWorld) -> resultFromHello + resultFromWorld)
        .thenCombine(hiCompletableFuture,
            (previousResult, currentResult) -> previousResult + currentResult)
        .thenApply(String::toUpperCase).join();

    timeTaken();

    return result;
  }

  public String helloWorld_4_async_calls() {
    startTimer();
    CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> hws.hello());
    CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> hws.world());
    CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
      delay(1000);
      return " Hi Completable Future";
    });
    CompletableFuture<String> byeCompletableFuture = CompletableFuture.supplyAsync(() -> {
      delay(1000);
      return " Bye!";
    });

    String result = hello
        .thenCombine(world,
            (resultFromHello, resultFromWorld) -> resultFromHello + resultFromWorld)
        .thenCombine(hiCompletableFuture,
            (previousResult, currentResult) -> previousResult + currentResult)
        .thenCombine(byeCompletableFuture,
            (previousResult, currentResult) -> previousResult + currentResult)
        .thenApply(String::toUpperCase).join();

    timeTaken();

    return result;
  }

  /**
   * thenCompose() is used when one completable future task depends on another completable future.
   * The way it works is the task function which is inside thenCompose() waits for the result from
   * its previous operation In below example, worldFuture() depends on hello() result It is similar
   * to thenApply() as it utilizes a function.
   */
  public CompletableFuture<String> completableFuture_thenCompose() {

    /**
     * worldFuture returns a completableFuture
     */
    return CompletableFuture.supplyAsync(() -> hws.hello())
        .thenCompose(previousResult -> hws.worldFuture(previousResult));
//        .thenApply(String::toUpperCase);
  }

  /**
   * API design of completable future execution is such that completion stage operations such as
   * thenCombine, thenApply are executed in the same thread It is done in order to prevent
   * additional cost of context switching by threads
   */
  public String helloWorld_combine_3_async_calls_log() {

    startTimer();
    CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> hws.hello());
    CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> hws.world());
    CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
      delay(1000);
      return " Hi Completable Future";
    });

    String result = hello
        .thenCombine(world,
            (resultFromHello, resultFromWorld) -> {
              log("thenCombine world");
              return resultFromHello + resultFromWorld;
            })
        .thenCombine(hiCompletableFuture, (previousResult, currentResult) -> {
          log("thenCombine hiCompetableFuture");
          return previousResult + currentResult;
        })
        .thenApply(str -> {
          log("thenApply");
          return str.toUpperCase();
        }).join();

    timeTaken();

    return result;
  }

  /**
   * TO change behavior of completion stage method to utilize separate threads instead of just using
   * the same thread, the completion stage methods have async variants along with their custom
   * thread pool overloaded versions
   * <p>
   * NOTE: But it is not a guarantee that separate thread will be used, it may only happen if the
   * thread is blocked
   */
  public String helloWorld_combine_3_async_calls_async() {

    startTimer();
    CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> hws.hello());
    CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> hws.world());
    CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
      delay(1000);
      return " Hi Completable Future";
    });

    String result = hello
        .thenCombineAsync(world,
            (resultFromHello, resultFromWorld) -> {
              log("thenCombine world");
              return resultFromHello + resultFromWorld;
            })
        .thenCombineAsync(hiCompletableFuture, (previousResult, currentResult) -> {
          log("thenCombine hiCompetableFuture");
          return previousResult + currentResult;
        })
        .thenApplyAsync(str -> {
          log("thenApply");
          return str.toUpperCase();
        }).join();

    timeTaken();

    return result;
  }

  public String helloWorld_combine_3_async_calls_customThreadPool() {

    startTimer();
    ExecutorService executorService = Executors.newFixedThreadPool(3);

    CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> hws.hello(),
        executorService);
    CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> hws.world(),
        executorService);
    CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
      delay(1000);
      return " Hi Completable Future";
    }, executorService);

    String result = hello
        .thenCombine(world,
            (resultFromHello, resultFromWorld) -> {
              log("thenCombine world");
              return resultFromHello + resultFromWorld;
            })
        .thenCombine(hiCompletableFuture, (previousResult, currentResult) -> {
          log("thenCombine hiCompetableFuture");
          return previousResult + currentResult;
        })
        .thenApply(str -> {
          log("thenApply");
          return str.toUpperCase();
        }).join();

    timeTaken();

    return result;
  }
}
