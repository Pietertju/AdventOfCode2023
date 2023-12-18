package Benchmarking;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Pieter
 */
public class Benchmark {
    
    public static long currentTime() {
        return System.nanoTime();
    }
    
    public static double elapsedTime(long time1, long time2) {
        double diff = time2-time1;
        double conversion = 1000000;
        return round(diff / conversion, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
