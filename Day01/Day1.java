package Day01;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Pieter
 */
public class Day1 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day01/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {               
               char[] lineCharacters = line.toCharArray();           
               answerPart1 += getFirstAndLastDigit(lineCharacters);
               
               line = line.replace("one", "one1one");
               line = line.replace("two", "two2two");
               line = line.replace("three", "three3three");
               line = line.replace("four", "four4four");
               line = line.replace("five", "five5five");
               line = line.replace("six", "six6six");
               line = line.replace("seven", "seven7seven");
               line = line.replace("eight", "eight8eight");
               line = line.replace("nine", "nine9nine");
               
               lineCharacters = line.toCharArray();           
               answerPart2 += getFirstAndLastDigit(lineCharacters);
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
    
    private static int getFirstAndLastDigit(char[] lineCharacters) {    
        boolean found = false;
        int firstNum = -1;
        int lastNum = -1;
        for(char c : lineCharacters) {
            if(Character.isDigit(c)) {
                if(found) {
                    lastNum = Character.getNumericValue(c);
                } else {
                    firstNum = Character.getNumericValue(c) * 10;
                    lastNum = Character.getNumericValue(c);
                    found = true;
                }
            }
        }
               
        return firstNum + lastNum;
    }
}
