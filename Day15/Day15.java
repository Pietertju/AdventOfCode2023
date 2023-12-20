package Day15;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 *
 * @author Pieter
 */
public class Day15 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day15/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        String[] inputs = new String[0];
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
                     
            while ((line = br.readLine()) != null) {
                line = line.trim();
                inputs = line.split(",");
            }                     
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        LinkedHashMap<String, Long>[] boxes = new LinkedHashMap[256];
        for(int i = 0; i < boxes.length; i++) {
            boxes[i] = new LinkedHashMap<>();
        }             
        
        for(int i = 0; i < inputs.length; i++) {
            int hash = computeHash(inputs[i]);
            answerPart1 += hash;
            
            String label = "";
            if(inputs[i].contains("-")) {
                label = inputs[i].substring(0,inputs[i].length()-1);
                int box = computeHash(label);
                boxes[box].remove(label);
            } else {
                label = inputs[i].split("\\=")[0];
                long focalLength = Long.parseLong(inputs[i].split("\\=")[1]);
                
                int box = computeHash(label);
                boxes[box].put(label, focalLength);
            }
        }
        int boxNumber = 1;
        for(LinkedHashMap<String, Long> hm : boxes) {
            int i = 0;
            for(long focalLength : hm.values()) {
                long score = boxNumber * (i+1) * focalLength;
                answerPart2 += score;
                i++;
            }
            boxNumber++;
        }
        
        long endTime = Benchmark.currentTime();
        double elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
    
    public static int computeHash(String s) {
        int hash = 0;
        
        for(int i = 0; i < s.length(); i++) {
            hash+= (int) s.charAt(i);
            hash*=17;
            
            hash %= 256;
        }
        
        return hash;
    }
    
    public static class Lens {
        String label;
        public Lens(String label) {
            this.label = label;
        }
    }
}