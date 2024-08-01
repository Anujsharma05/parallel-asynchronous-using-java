package com.learnjava.completableFuture;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.startTimer;
import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.CommonUtil.timeTaken;
import static java.lang.Thread.sleep;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PracticeScenarios {

  public static void main(String[] args) {
    PracticeScenarios practice = new PracticeScenarios();
//    practice.test();
    practice.methodTest();
  }

  public void test() {

    CompletableFuture<String> cf = getCF();
    stopWatch.reset();
    stopWatch.start();
    String res = cf.join();
    stopWatch.stop();
    System.out.println("join time: " + stopWatch.getTime());
    System.out.println(res);
  }

  public CompletableFuture<String> getCF() {
    stopWatch.start();
    CompletableFuture<String> cf=  CompletableFuture.supplyAsync(() -> {
      hello();
      return "something else";
    });
    stopWatch.stop();
    System.out.println("supply async time: " + stopWatch.getTime());
    return cf;
  }

  public void methodTest() {
    CompletableFuture<String> one = CompletableFuture.supplyAsync(() -> methodOne());
    CompletableFuture<String> two = CompletableFuture.supplyAsync(() -> methodTwo());
    CompletableFuture<String> three = CompletableFuture.supplyAsync(() -> methodThree());
    CompletableFuture<String> four = CompletableFuture.supplyAsync(() -> methodFour());
    CompletableFuture<String> five = CompletableFuture.supplyAsync(() -> methodFive());

    List<CompletableFuture<String>> cfList = List.of(one, two, three, four, five);

//    String output = cfList.stream().map(CompletableFuture::join).collect(Collectors.joining(","));

//    List<String> outputList = new ArrayList<>();
//    for(CompletableFuture<String> cf: cfList) {
//      startTimer();
//      String s = cf.join();
//      System.out.println("string added: " + s);
//      outputList.add(s);
//      timeTaken();
//      stopWatch.reset();
//    }
//    System.out.println(outputList);

    joinOneToThree(one, two, three);

    stopWatch.reset();

    try {
      sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    joinFourToFive(four, five);
  }

  public void joinOneToThree(CompletableFuture<String> one, CompletableFuture<String> two,
      CompletableFuture<String> three) {
    startTimer();
    one.join();
    two.join();
    three.join();
    timeTaken();
  }

  public void joinFourToFive(CompletableFuture<String> four, CompletableFuture<String> five) {
    startTimer();
    four.join();
    five.join();
    timeTaken();
  }

  public String methodOne() {
    delay(5000);
    System.out.println("one");
    return "one";
  }
  public String methodTwo() {
    delay(5000);
    System.out.println("two");
    return "two";
  }
  public String methodThree() {
    delay(5000);
    System.out.println("three");
    return "three";
  }
  public String methodFour() {
    delay(9000);
    System.out.println("four");
    return "four";
  }
  public String methodFive() {
    delay(5000);
    System.out.println("five");
    return "five";
  }

  public void hello() {
    delay(5000);
    System.out.println("hello");
//    return "";
  }
}
