package com.learnjava.completableFuture;

import static org.junit.jupiter.api.Assertions.*;

import com.learnjava.service.HelloWorldService;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class CompletableFutureHelloWorldTest {

  HelloWorldService hws = new HelloWorldService();
  CompletableFutureHelloWorld cfhw = new CompletableFutureHelloWorld(hws);

  @Test
  void helloWorld_withSize() {
    CompletableFuture<String> completableFuture = cfhw.helloWorld_withSize();
    completableFuture.thenAccept(result -> assertEquals("11 - HELLO WORLD", result)).join();
  }

  @Test
  void helloWorld_combineAsyncCalls() {
    String result = cfhw.helloWorld_combine_async_calls();
    assertEquals("HELLO WORLD!", result);
  }

  @Test
  void helloWorld_combine3AsyncCalls() {
    String result = cfhw.helloWorld_combine_3_async_calls();
    assertEquals("HELLO WORLD! HI COMPLETABLE FUTURE", result);
  }

  @Test
  void helloWorld_combine4AsyncCalls() {
    String result = cfhw.helloWorld_4_async_calls();
    assertEquals("HELLO WORLD! HI COMPLETABLE FUTURE BYE!", result);
  }

  @Test
  void helloWorld_completableFuture_thenCompose() {
    CompletableFuture<String> completableFuture = cfhw.completableFuture_thenCompose();
    completableFuture.thenAccept(result -> assertEquals("hello world!", result)).join();
  }

  @Test
  void helloWorld_combine3AsyncCalls_log() {
    String result = cfhw.helloWorld_combine_3_async_calls_log();
    assertEquals("HELLO WORLD! HI COMPLETABLE FUTURE", result);
  }

  @Test
  void helloWorld_combine3AsyncCalls_async() {
    String result = cfhw.helloWorld_combine_3_async_calls_async();
    assertEquals("HELLO WORLD! HI COMPLETABLE FUTURE", result);
  }

  @Test
  void helloWorld_combine3AsyncCalls_customThreadPool() {
    String result = cfhw.helloWorld_combine_3_async_calls_customThreadPool();
    assertEquals("HELLO WORLD! HI COMPLETABLE FUTURE", result);
  }
}