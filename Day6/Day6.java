package Day6;

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
        File file = new File("res/day6/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {               
               
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
    }
}
