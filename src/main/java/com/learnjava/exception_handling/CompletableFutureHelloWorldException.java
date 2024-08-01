package com.learnjava.exception_handling;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.startTimer;
import static com.learnjava.util.CommonUtil.timeTaken;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.service.HelloWorldService;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureHelloWorldException {

  private HelloWorldService hws;

  public CompletableFutureHelloWorldException(HelloWorldService hws) {
    this.hws = hws;
  }

  /**
   * handle() method runs when there is an exception in the call
   * But even if there is no exception, the handle method will still run
   * It will recover from exception with provided recoverable value
   */
  public String helloWorld_combine_3_async_calls_withHandle() {

    startTimer();
    CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> hws.hello());
    CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> hws.world());
    CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
      delay(1000);
      return " Hi Completable Future";
    });

    String result =  hello
        .handle((res, ex) -> {
          log("res" + res);
          if(ex != null) {
            log("Exception in hello: " + ex);
            /**
             * Recoverable value
             */
            return "dummy";
          }
          return res;
        })
        .thenCombine(world,
            (resultFromHello, resultFromWorld) -> resultFromHello + resultFromWorld)
        .handle((res, ex) -> {
          System.out.println("The above bifunction wont work if first method succeed and second fail so the two string wont add up");
          if(ex != null) {
            log("Exception in world: " + ex);
            return "tummy";
          }

          return res;
        })
        .thenCombine(hiCompletableFuture, (previousResult, currentResult) -> previousResult + currentResult)
        .thenApply(String::toUpperCase).join();

    timeTaken();

    return result;
  }

  /**
   * exceptionally() method runs only when there is an exception in the call
   * But if there is no exception, exceptionally() won't run
   * It will recover from exception with provided recoverable value
   */
  public String helloWorld_combine_3_async_calls_withExceptionally() {

    startTimer();
    CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> hws.hello());
    CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> hws.world());
    CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
      delay(1000);
      return " hi";
    });

    String result =  hello
        .exceptionally(ex -> {
          log("Exception in hello: " + ex);
          /**
           * Recoverable value
           */
          return "dummy";
        })
        .thenCombine(world,
            (resultFromHello, resultFromWorld) -> resultFromHello + resultFromWorld)
        .exceptionally(ex -> {
          log("Exception in world: " + ex);
          return "tummy";
        })
        .thenCombine(hiCompletableFuture, (previousResult, currentResult) -> previousResult + currentResult)
        .thenApply(String::toUpperCase).join();

    timeTaken();

    return result;
  }

  /**
   * whenComplete() method runs when there is an exception as well as when there is no exception
   * But whenComplete() cannot recover from the exception, it will just throw it further
   */
  public String helloWorld_combine_3_async_calls_withWhenComplete() {

    startTimer();
    CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> hws.hello());
    CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> hws.world());
    CompletableFuture<String> hiCompletableFuture = CompletableFuture.supplyAsync(() -> {
      delay(1000);
      return " Hi";
    });

    String result =  hello
        .whenComplete((res, ex) -> {
          log("res" + res);
          log("Exception in hello: " + ex);
        })
        .thenCombine(world,
            (resultFromHello, resultFromWorld) -> resultFromHello + resultFromWorld)
        .whenComplete((res, ex) -> {
          log("res" + res);
          log("Exception in world: " + ex);
        })
        .thenCombine(hiCompletableFuture, (previousResult, currentResult) -> previousResult + currentResult)
        .thenApply(String::toUpperCase).join();

    timeTaken();

    return result;
  }

}
