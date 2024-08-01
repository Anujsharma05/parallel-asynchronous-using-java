package com.learnjava.forkjoin;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.util.DataSet;
import java.util.List;
import java.util.stream.Collectors;

public class ParallelStream {

  public static void main(String[] args) {

    stopWatch.start();
    List<String> resultList;
    List<String> names = DataSet.namesList();
    log("names : " + names);

    resultList = names.parallelStream().map(ParallelStream::addNameLengthTransform).collect(
        Collectors.toList());

    stopWatch.stop();
    log("Final Result : " + resultList);
    log("Total Time Taken : " + stopWatch.getTime());
  }


  private static String addNameLengthTransform(String name) {
    delay(500);
    return name.length() + " - " + name;
  }
}
