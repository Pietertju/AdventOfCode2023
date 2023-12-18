package Day06;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Pieter
 */
public class Day6 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day06/input.txt");
        
        long answerPart1 = 1;
        long answerPart2 = 0;
        
        long[] times = new long[0];
        long[] distances = new long[0];
        
        long raceTime = 0;
        long raceDistance = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {               
                if(line.startsWith("Time:")) {
                    String timeString = line.split(":")[1].trim();
                    String[] timeStrings = timeString.split("\\s+");
                    
                    times = new long[timeStrings.length];
                    raceTime = Long.parseLong(timeString.replace(" ", ""));
                    
                    for(int i  = 0; i < timeStrings.length; i++) {
                        times[i] = Long.parseLong(timeStrings[i]);
                    }
                }
                
                if(line.startsWith("Distance:")) {
                    String distanceString = line.split(":")[1].trim();
                    String[] distanceStrings = distanceString.split("\\s+");
                    
                    distances = new long[distanceStrings.length];
                    raceDistance = Long.parseLong(distanceString.replace(" ", ""));
                    
                    for(int i  = 0; i < distanceStrings.length; i++) {
                        distances[i] = Long.parseLong(distanceStrings[i]);
                    }
                }
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        // part 1
        long[] possibleTimes = new long[times.length];
        
        for(int i = 0; i < times.length; i++) {
            possibleTimes[i] += getPossibilitiesABC(times[i], distances[i]);
        }
        
        for(long possibilities : possibleTimes) {
            answerPart1 *= possibilities;
        }
        
        // part 2
        answerPart2 = getPossibilitiesABC(raceTime, raceDistance);
        
        long endTime = Benchmark.currentTime();
        double elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
    
    public static long getPossibilitiesABC(long raceTime, long raceDistance) {
        long a = 1;
        long b = raceTime;
        long c = raceDistance + 1;
        
        long d = (b*b) - (4*a*c);
        
        double start = Math.abs((-b - Math.sqrt(d))/(2*a));
        double end =  Math.abs((-b + Math.sqrt(d))/(2*a));
        
        long startHoldTime =  (long) Math.ceil(Math.min(start, end));
        long endHoldTime =  (long) Math.floor(Math.max(start, end));
        if(endHoldTime > raceTime-1) {
            endHoldTime = raceTime - 1;
        }
        
        return endHoldTime - startHoldTime + 1;
    }
}
