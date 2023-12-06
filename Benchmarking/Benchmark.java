package Benchmarking;

/**
 *
 * @author Pieter
 */
public class Benchmark {
    
    public static long currentTime() {
        return System.nanoTime();
    }
    
    public static long elapsedTime(long time1, long time2) {
        return (time2-time1)/1000000;
    }
}
