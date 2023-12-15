package Day15;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
        
        int lines = 0;
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
        
        ArrayList<Lens>[] boxes = new ArrayList[256];
        for(int i = 0; i < boxes.length; i++) {
            boxes[i] = new ArrayList<>();
        }             
        
        for(int i = 0; i < inputs.length; i++) {
            int hash = computeHash(inputs[i]);
            answerPart1 += hash;
            
            
            String label = "";
            long vocalLength = -1;
            if(inputs[i].contains("-")) {
                label = inputs[i].substring(0,inputs[i].length()-1);
                int box = computeHash(label);
                boolean alreadyThere = false;
                for(int j = 0; j < boxes[box].size(); j++) {
                    if(boxes[box].get(j).label.equals(label)) {
                        boxes[box].remove(j);
                        break;
                    }
                }
            } else {
                label = inputs[i].split("\\=")[0];
                vocalLength = Long.parseLong(inputs[i].split("\\=")[1]);
                
                int box = computeHash(label);
                boolean alreadyThere = false;
                for(int j = 0; j < boxes[box].size(); j++) {
                    if(boxes[box].get(j).label.equals(label)) {
                        boxes[box].get(j).setVocalLength(vocalLength);
                        alreadyThere = true;
                        break;
                    }
                }
                
                if(!alreadyThere) {
                    Lens lens = new Lens(label);
                    lens.setVocalLength(vocalLength);
                    boxes[box].add(lens);
                }
            }
        }
        int boxNumber = 1;
        for(ArrayList<Lens> lensList : boxes) {
            for(int i = 0; i < lensList.size(); i++) {
                long score = boxNumber * (i+1) * lensList.get(i).focalLength;
                answerPart2 += score;
            }
            boxNumber++;
        }
        
        long endTime = Benchmark.currentTime();
        long elapsed = Benchmark.elapsedTime(startTime, endTime);
     
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
        long focalLength;
        public Lens(String label) {
            this.label = label;
        }
        
        void setVocalLength(long focalLength) {
            this.focalLength = focalLength;
        }
    }
}