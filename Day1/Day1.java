/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Day1;

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
        File file = new File("res/day1/input.txt");
        
        long answer = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Comment out replaces for part 1 answer
               line = line.replace("one", "one1one");
               line = line.replace("two", "two2two");
               line = line.replace("three", "three3three");
               line = line.replace("four", "four4four");
               line = line.replace("five", "five5five");
               line = line.replace("six", "six6six");
               line = line.replace("seven", "seven7seven");
               line = line.replace("eight", "eight8eight");
               line = line.replace("nine", "nine9nine");
               char[] lineCharacters = line.toCharArray();
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
               
               int sum = firstNum + lastNum;
               answer += sum;
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        System.out.println(answer);
    }
}
