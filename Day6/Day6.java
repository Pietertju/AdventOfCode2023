package Day6;

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
        File file = new File("res/day6/input.txt");
        
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
            long time = times[i];
            for(long holdTime = 1; holdTime < time; holdTime++) {
                long distance = holdTime * (time-holdTime);
                if(distance > distances[i]) {
                    possibleTimes[i]++;
                }
            }
        }
        
        for(long possibilities : possibleTimes) {
            answerPart1 *= possibilities;
        }
        
        // part 2
        for(long holdTime = 1; holdTime < raceTime; holdTime++) {
            long distance = holdTime * (raceTime-holdTime);
            if(distance > raceDistance) {
                answerPart2++;
            }
        } 
        long endTime = Benchmark.currentTime();
        long elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
}
