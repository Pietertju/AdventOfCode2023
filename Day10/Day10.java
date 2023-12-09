package Day10;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Pieter
 */
public class Day10 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day10/test.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        int lines = 0;
       
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
                     
            while ((line = br.readLine()) != null) {
                
            }                     
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        long endTime = Benchmark.currentTime();
        long elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
}
