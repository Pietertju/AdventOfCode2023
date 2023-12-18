package Day12;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Pieter
 */
public class Day12 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day12/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        long test = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
                     
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] split = line.split(" ");
                
                String inputString = split[0];
                
                String inputStringPart2 = inputString;
                for(int i = 0; i < 4; i++) {
                    inputStringPart2 = inputStringPart2 +"?"+inputString;
                }
                                
                String[] numStrings = split[1].split(",");
                
                int[] numbers = new int[numStrings.length];
                int[] numbersPart2 = new int[numbers.length * 5];
                
                int totalNumbers = 0;
                
                //part 1
                for(int i = 0 ; i < numbers.length; i++) {
                    numbers[i] = Integer.parseInt(numStrings[i]);
                    totalNumbers += numbers[i];
                }
                
                //part 2
                int part2Index = 0;
                for(int j = 0; j < 5; j++) {
                    for(int number : numbers) {
                        numbersPart2[part2Index] = number;
                        part2Index++;
                    }
                }
                
                int totalNumbersPart2 = 5*totalNumbers;
                
                // Add padding
                inputString = "." + inputString + ".";
                inputStringPart2 = "." + inputStringPart2 + ".";
                
                // Simplify string
                inputString = inputString.replaceAll("\\.{2,}", ".");
                inputStringPart2 = inputStringPart2.replaceAll("\\.{2,}", ".");
                boolean[] brokenSequence = new boolean[totalNumbers + numbers.length + 1];               
                boolean[] brokenSequencePart2 = new boolean[(totalNumbersPart2 + numbersPart2.length) + 1];
                
                //part 1
                int sequenceIndex = 1;
                for(int i = 0; i < numbers.length; i++) {
                    for(int j = 0; j < numbers[i]; j++) {
                        brokenSequence[sequenceIndex]=true;
                        sequenceIndex++;
                    }
                    sequenceIndex++;
                }            
                
                //part 2
                sequenceIndex = 1;
                for(int i = 0; i < numbersPart2.length; i++) {
                    for(int j = 0; j < numbersPart2[i]; j++) {
                        brokenSequencePart2[sequenceIndex]=true;
                        sequenceIndex++;
                    }
                    sequenceIndex++;
                }
                
                answerPart1 += possibleSolutions(inputString.toCharArray(), brokenSequence);
                answerPart2 += possibleSolutions(inputStringPart2.toCharArray(), brokenSequencePart2);
            }                     
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        long endTime = Benchmark.currentTime();
        double elapsed = Benchmark.elapsedTime(startTime, endTime);
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
    
    private static long possibleSolutions(char[] input, boolean[] brokenSequence){
        //dp[i][j] = hoeveel mogelijkheden when matching chars[1 to i] with springs[1 to j]
        long[][] dp = new long[input.length][brokenSequence.length];

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < Math.min(i+1, brokenSequence.length); j++) {
                boolean broken = false;
                boolean working = false;
                
                if(input[i] == '#') {
                    broken = true;
                } else if(input[i] == '.') {
                    working = true;
                } else if(input[i] == '?') {
                    // both are possible
                    working = true;
                    broken = true;
                }
                
                long possibleWays = 0;      
                if(broken && brokenSequence[j]){
                    if(i-1 == -1 && j-1 == -1) possibleWays = 1;
                    else if (i-1 < 0 || j-1 < 0)possibleWays = 0;
                    else {
                        possibleWays = dp[i-1][j-1];
                    }
                } else if (working && !brokenSequence[j]) { 
                    if(i-1 == -1 && j-1 == -1) possibleWays = 1;
                    else if (i-1 >= 0 && j-1 == -1) possibleWays = dp[i-1][j];
                    else if (i-1 < 0 || j-1 < 0) possibleWays = 0;
                    else {
                        possibleWays += dp[i-1][j-1] + dp[i-1][j];   
                    }
                }
                dp[i][j] = possibleWays;
            }
        }
        //prettyPrint(dp, input, brokenSequence);
        return dp[input.length-1][brokenSequence.length-1];
    }
    
    public static void prettyPrint(long[][] array, char[] input, boolean[] broken) {
        for (int i = 0; i < array.length; i++) {
            System.out.print(input[i] + ":  ");
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println("");                    
        }
        System.out.print("    ");
        for(int i = 0; i < broken.length; i++) {
            System.out.print((broken[i] ? "T" : "F") + " ");
        }
        System.out.println("");
    }
}