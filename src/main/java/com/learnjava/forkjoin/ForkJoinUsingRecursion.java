package com.learnjava.forkjoin;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

import com.learnjava.util.DataSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkJoinUsingRecursion extends RecursiveTask<List<String>> {

    private List<String> inputList;

    public ForkJoinUsingRecursion(List<String> inputList) {
        this.inputList = inputList;
    }

    public static void main(String[] args) {

        stopWatch.start();
        List<String> resultList;
        List<String> names = DataSet.namesList();
        log("names : "+ names);

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinUsingRecursion forkJoinTask = new ForkJoinUsingRecursion(names);

        resultList = forkJoinPool.invoke(forkJoinTask);

        stopWatch.stop();
        log("Final Result : "+ resultList);
        log("Total Time Taken : "+ stopWatch.getTime());
    }


    private static String addNameLengthTransform(String name) {
        delay(500);
        return name.length()+" - "+name ;
    }

    @Override
    protected List<String> compute() {

        //base condition
        if(inputList.size() <= 1) {
            List<String> result = new ArrayList<>();
            inputList.stream().forEach(value -> result.add(addNameLengthTransform(value)));
            return result;
        }

        int midPoint = inputList.size()/2;
        ForkJoinTask<List<String>> leftHalfTask = new ForkJoinUsingRecursion(inputList.subList(0, midPoint)).fork();

        inputList = inputList.subList(midPoint, inputList.size());

        List<String> rightHalfResult = compute();
        List<String> leftResult = leftHalfTask.join();
        leftResult.addAll(rightHalfResult);

        return leftResult;
    }
}
