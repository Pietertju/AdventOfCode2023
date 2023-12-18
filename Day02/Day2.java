package Day02;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Pieter
 */
public class Day2 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day02/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] idAndSamples = line.split(": ");
                
                long gameId = Long.parseLong(idAndSamples[0].split(" ")[1]);
                
                String samples = idAndSamples[1];                       
                String[] sample = samples.split(";");
                
                long minRed = 0;
                long minBlue = 0;
                long minGreen = 0;
                
                boolean possibleGame = true;
                
                for(String entry : sample) {
                    String[] balls = entry.split(",");
                    
                    for(String ball : balls) {
                        ball = ball.trim();                    
                        String[] amountAndColor = ball.split(" ");
                        
                        long amount = Long.parseLong(amountAndColor[0]);
                        String color = amountAndColor[1];
                        
                        if(color.equals("red")) {
                            if(amount > 12) possibleGame = false;
                            if(minRed < amount) minRed = amount;
                        } else if(color.equals("green")) {
                            if(amount > 13) possibleGame = false;
                            if(minGreen < amount) minGreen = amount;
                        } else if(color.equals("blue")) {
                            if(amount > 14) possibleGame = false;
                            if(minBlue < amount) minBlue = amount;
                        }
                    }
                }
                
                if(possibleGame) answerPart1 += gameId;
                
                long power = minRed * minBlue * minGreen;
                answerPart2 += power;
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
}
