package com.learnjava.parallel_streams;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.stopWatch;

import com.learnjava.util.DataSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SequentialParallelStream {

  public static void main(String[] args) {
    List<String> names = DataSet.namesList();
    stopWatch.start();
    List<String> result = performOperation(names, false);
    stopWatch.stop();

    System.out.println("Time: " + stopWatch.getTime());
    System.out.println(result);
  }

  /**
   * There are two methods available on stream object to decide whether the
   * stream should be executed sequentially or parallel
   * stream.sequential()
   * stream.parallel()
   */
  static List<String> performOperation(List<String> names, boolean isParallel) {
    Stream<String> stream = names.stream();

    if(isParallel) {
      stream.parallel();
    }

    return stream.map(name -> addNameLengthTransform(name)).collect(Collectors.toList());
  }

  private static String addNameLengthTransform(String name) {
    delay(500);
    return name.length() + " - " + name;
  }

  /**
   * Section 6 Exercise solution
   */
  static List<String> string_toLowerCase(List<String> names, boolean isParallel) {
    Stream<String> stream = names.stream();
    if(isParallel) {
      stream.parallel();
    }
    return stream.map(String::toLowerCase).collect(Collectors.toList());
  }
}
