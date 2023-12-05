package Day4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Pieter
 */
public class Day4 {
    public static void main(String[] args) {
        File file = new File("res/day2/input.txt");
        
        long answer = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        System.out.println(answer);
    }
}
