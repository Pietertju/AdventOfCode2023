package Day24;

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
public class Day24 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day24/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        ArrayList<Hail> hails = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                String[] split = line.split("@");
                String[] location = split[0].split(",");
                String[] velocities = split[1].split(",");
                
                double x = Double.parseDouble(location[0].trim());
                double y = Double.parseDouble(location[1].trim());
                double z = Double.parseDouble(location[2].trim());
                
                double vx = Double.parseDouble(velocities[0].trim());
                double vy = Double.parseDouble(velocities[1].trim());
                double vz = Double.parseDouble(velocities[2].trim());
                
                Hail hail = new Hail(x, y, z, vx, vy, vz);
                hails.add(hail);
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        double test1 = 200000000000000d;
        double test2 = 400000000000000d;
        
        long parseEnd = Benchmark.currentTime();
        for(int i = 0; i < hails.size(); i++) {
            Hail hailA = hails.get(i);
            for(int j = i+1; j < hails.size(); j++) {
                Hail hailB = hails.get(j);
                if(hailA.ax == hailB.ax) continue;
                double leftSide = hailA.ax - hailB.ax;
                double rightSide = hailB.b - hailA.b;
                double x = rightSide / leftSide;

                double y = (hailA.ax * x) + hailA.b;
                double stepsForA = (x - hailA.x)  / hailA.vx;
                double stepsForB = (x -hailB.x) / hailB.vx;
                
                if(y >= test1 && y <= test2 &&
                    x >= test1 && x <= test2) {
                    if(stepsForA >= 0 && stepsForB >= 0) {
                        answerPart1++;
                    }
                }
            }
        }
        
        long betweenTime = Benchmark.currentTime();
        
        //Part 2
         for(int i = 0; i < hails.size(); i++) {
            Hail hailA = hails.get(i);
            for(int j = i+1; j < hails.size(); j++) {
                Hail hailB = hails.get(j);
                
            }
        }
        answerPart2 = 0;
        
        long endTime = Benchmark.currentTime();
        
        double elapsedParse = Benchmark.elapsedTime(startTime, parseEnd);
        double elapsedPart1 = Benchmark.elapsedTime(parseEnd, betweenTime);
        double elapsedPart2 = Benchmark.elapsedTime(betweenTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Parsing input took: " + elapsedParse + " ms");
        System.out.println("Part 1 took: " + elapsedPart1 + " ms");
        System.out.println("Part 2 took: " + elapsedPart2 + " ms");
    }
    
    public static class Hail {
        double x;
        double y;
        double z;
        
        double vx;
        double vy;
        double vz;
        
        double ax;
        double b;
        
        public Hail(double x, double y, double z, double vx, double vy, double vz) {
            this.x = x;
            this.y = y;
            this.z = z;
            
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            ax = this.vy / this.vx;
            double steps = this.x / this.vx;
            b = y - (vy * steps);
         }
    }
}
