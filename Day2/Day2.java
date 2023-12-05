/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Day2;

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
        File file = new File("res/day2/input.txt");
        
        long answer = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String samples = line.split(": ")[1];
                String[] sample = samples.split(";");
                long minRed = 0;
                long minBlue = 0;
                long minGreen = 0;
                for(String entry : sample) {
                    String[] balls = entry.split(",");
                    for(String ball : balls) {
                        ball = ball.trim();
                        String[] amountAndColor = ball.split(" ");
                        long amount = Long.parseLong(amountAndColor[0]);
                        String color = amountAndColor[1];
                        if(color.equals("red")) {
                            if(minRed < amount) minRed = amount;
                        } else if(color.equals("green")) {
                            if(minGreen < amount) minGreen = amount;
                        } else if(color.equals("blue")) {
                            if(minBlue < amount) minBlue = amount;
                        }
                    }
                }
                
                long power = minRed * minBlue * minGreen;
                answer += power;
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        System.out.println(answer);
    }
}
