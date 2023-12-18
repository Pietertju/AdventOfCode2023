package Benchmarking;

/**
 *
 * @author Pieter
 */
public class Benchmark {
    
    public static long currentTime() {
        return System.nanoTime();
    }
    
    public static double elapsedTimeNano(long time1, long time2) {
        double diff = time2-time1;
        double conversion = 1000000;
        return diff / conversion;
    }
    
    public static long elapsedTime(long time1, long time2) {
        return (time2-time1)/1000000;
    }
}
