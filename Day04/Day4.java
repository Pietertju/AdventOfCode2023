package Day04;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Pieter
 */
public class Day4 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day04/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        int lines = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        long[] numberOfCards = new long[lines];
        Arrays.fill(numberOfCards, 1);
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
                     
            while ((line = br.readLine()) != null) {
                String[] idAndNumbers = line.split(": ");
                int gameId = Integer.parseInt(idAndNumbers[0].split("\\s+")[1]);

                String[] ticketNumbersStrings = idAndNumbers[1].trim().split(" \\| ");
                String[] winningNumbers = ticketNumbersStrings[0].split("\\s+");
                String[] ticketNumbers = ticketNumbersStrings[1].split("\\s+");
                
                
                long ticketValue = 1;
                long amountOfWinningNumbers = 0;
                            
                for(String ticketNumber : ticketNumbers) {
                    if(contains(winningNumbers, ticketNumber)) {
                        amountOfWinningNumbers++;
                        ticketValue *= 2;                       
                    }
                }
                
                for(int i = 1; i <= amountOfWinningNumbers; i++) {
                    numberOfCards[gameId - 1 + i] += numberOfCards[gameId - 1];
                }
                
                answerPart1 += (ticketValue / 2);
            }
            
            for(long amountOfCards : numberOfCards) {
                answerPart2 += amountOfCards;
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
    
    public static boolean contains(String[] array, String searchValue) {
        for (String entry : array) {
            if (entry.equals(searchValue)) {
                return true;
            }
        }
        return false;
    }
}
