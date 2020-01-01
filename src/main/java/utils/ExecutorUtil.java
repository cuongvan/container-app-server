/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cuong
 */
public class ExecutorUtil {
    public static ThreadPoolExecutor newExecutor(int minThreads, int maxThreads) {
        return new ThreadPoolExecutor(
            minThreads, maxThreads,
            10L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }
}
