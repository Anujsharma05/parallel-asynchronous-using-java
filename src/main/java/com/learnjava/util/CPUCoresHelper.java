package com.learnjava.util;

/**
 * Return total cores of the system cpu
 */
public class CPUCoresHelper {
  public static int totalCores() {
    return Runtime.getRuntime().availableProcessors();
  }
}
