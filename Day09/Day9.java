package Day09;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Pieter
 */
public class Day9 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day9/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
   
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
                     
            while ((line = br.readLine()) != null) {
                String[] numberStrings = line.split("\\s+");
                long[] values = new long[numberStrings.length];
                for(int i = 0; i < values.length; i++) {
                    values[i] = Long.parseLong(numberStrings[i]);
                }
                
                answerPart1 += predict(values, false);
                
                long part2Prediction = predict(values, true);
                answerPart2 += part2Prediction;
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
    
    private static long predict(long[] numbers, boolean part2) {     
        boolean allZeroes = false;
        
        long[] relevantDifferences = new long[numbers.length-2];
      
        int relevantIndex = 0;
        long[] differences = numbers;
        
        while(!allZeroes) {
            Differences diff = computeDifferences(differences);
            differences = diff.differences;
            allZeroes = diff.allZeroes;
            relevantDifferences[relevantIndex] = (part2) ? differences[0] : differences[differences.length-1];     
            relevantIndex++;
        }
        
        long prediction = 0;
        for(int i = relevantIndex-1; i >= 0; i--) {
            prediction = (part2) ? relevantDifferences[i] - prediction : prediction + relevantDifferences[i];
        }
        
        if(part2) return numbers[0] - prediction;
        return prediction + numbers[numbers.length - 1];
    }
    
    private static Differences computeDifferences(long[] numbers) {
       long[] differences = new long[numbers.length-1];
       boolean allZeroes = true;
       
       for(int i = 0; i < differences.length; i++) {
           differences[i] = numbers[i+1] - numbers[i];
           if(differences[i] != 0) allZeroes = false;
       }
       
       Differences diff = new Differences(differences, allZeroes);
       return diff;
    }
    
    private static class Differences  {
        long[] differences;
        boolean allZeroes;
        private Differences(long[] differences, boolean allZeroes) {
            this.differences = differences;
            this.allZeroes = allZeroes;
        }
    }
}
