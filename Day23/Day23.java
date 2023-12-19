package Day23;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Pieter
 */
public class Day23 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day23/test.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
                        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        
        long parseEnd = Benchmark.currentTime();
        
        //Part 1
        answerPart1 = 0;
        
        long betweenTime = Benchmark.currentTime();
        
        //Part 2
        answerPart2 = 0;
        
        long endTime = Benchmark.currentTime();
        
        double elapsedParse = Benchmark.elapsedTime(startTime, parseEnd);
        double elapsedPart1 = Benchmark.elapsedTime(parseEnd, betweenTime);
        double elapsedPart2 = Benchmark.elapsedTime(betweenTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Parsing input took: " + elapsedParse + " ms");
        System.out.println("Part 1 took: " + elapsedPart1 + " ms");
        System.out.println("Part 2 took: " + elapsedPart2 + " ms");
    }
}
