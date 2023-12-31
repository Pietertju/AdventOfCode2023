package Day24;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 *
 * @author Pieter
 */
public class Day24 {
    
    static MathContext mc = new MathContext(32, RoundingMode.HALF_UP);
    
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
                
                BigDecimal x = new BigDecimal(location[0].trim());
                BigDecimal y = new BigDecimal(location[1].trim());
                BigDecimal z = new BigDecimal(location[2].trim());
                Vector pos = new Vector(x, y, z);
                
                BigDecimal vx = new BigDecimal(velocities[0].trim());
                BigDecimal vy = new BigDecimal(velocities[1].trim());
                BigDecimal vz = new BigDecimal(velocities[2].trim());
                Vector vel = new Vector(vx, vy, vz);
                
                Hail hail = new Hail(pos, vel);
                hails.add(hail);
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        BigDecimal test1 = BigDecimal.valueOf(200000000000000l);
        BigDecimal test2 = BigDecimal.valueOf(400000000000000l);
        
        long parseEnd = Benchmark.currentTime();
        for(int i = 0; i < hails.size(); i++) {
            Hail hailA = hails.get(i);
            for(int j = i+1; j < hails.size(); j++) {
                Hail hailB = hails.get(j);
                if(hailA.ax.compareTo(hailB.ax) == 0) continue;
                BigDecimal leftSide = hailA.ax.subtract(hailB.ax);
                BigDecimal rightSide = hailB.b.subtract(hailA.b);
                BigDecimal x = rightSide.divide(leftSide, mc);

                BigDecimal y = (hailA.ax.multiply(x)).add(hailA.b);
                BigDecimal stepsForA = (x.subtract(hailA.pos.x)).divide(hailA.vel.x, mc);
                BigDecimal stepsForB = (x.subtract(hailB.pos.x)).divide(hailB.vel.x, mc);
                
                if(y.compareTo(test1) >= 0 && y.compareTo(test2) <= 0 &&
                    x.compareTo(test1) >= 0 && x.compareTo(test2) <= 0) {
                    if(stepsForA.compareTo(BigDecimal.ZERO) >= 0 && stepsForB.compareTo(BigDecimal.ZERO) >= 0) {
                        answerPart1++;
                    }
                }
            }
        }
        
        long betweenTime = Benchmark.currentTime();
        
        //Part 2
        Hail firstHail = hails.get(0);
        
        Hail secondHail = hails.get(1);
        Vector firstPlaneVector = subtractVectors(secondHail.pos, firstHail.pos);
        Vector secondPlaneVector = subtractVectors(addVectors(secondHail.pos, subtractVectors(secondHail.vel, firstHail.vel)), firstHail.pos);
        Vector normal1 = crossProduct(secondPlaneVector, firstPlaneVector);
        
        Hail thirdHail = hails.get(2);
        Vector firstPlaneVector2 = subtractVectors(thirdHail.pos, firstHail.pos);
        Vector secondPlaneVector2 = subtractVectors(addVectors(thirdHail.pos, subtractVectors(thirdHail.vel, firstHail.vel)), firstHail.pos);
        Vector normal2 = crossProduct(secondPlaneVector2, firstPlaneVector2);
        
        Vector finalDir = crossProduct(normal1, normal2);
        finalDir.toUnitVector();
        
        int index = 1;
        Hail hailA = new Hail(hails.get(index).pos, subtractVectors(hails.get(index).vel, firstHail.vel)); 
        Hail hailB = new Hail(firstHail.pos, finalDir);
        
        while(hailA.ax.compareTo(hailB.ax) == 0 || hailA.vel.x.compareTo(BigDecimal.ZERO) == 0 || hailA.vel.y.compareTo(BigDecimal.ZERO) == 0) {
            index++;
            hailA = new Hail(hails.get(index).pos, subtractVectors(hails.get(index).vel, firstHail.vel));
        } 
        
        BigDecimal leftSide = (hailA.ax).subtract((hailB.ax));
        BigDecimal rightSide = (hailB.b).subtract((hailA.b));
        
        BigDecimal x = rightSide.divide(leftSide, mc);
        
        BigDecimal stepsForCrossing = (x.subtract((hailA.pos.x))).divide((hailA.vel.x), mc);
                
        Vector startPos = new Vector(
            (hailA.pos.x.add(hailA.vel.x.multiply(stepsForCrossing))).subtract(hailB.vel.x.multiply(stepsForCrossing)), 
            (hailA.pos.y.add(hailA.vel.y.multiply(stepsForCrossing))).subtract(hailB.vel.y.multiply(stepsForCrossing)), 
            (hailA.pos.z.add(hailA.vel.z.multiply(stepsForCrossing))).subtract(hailB.vel.z.multiply(stepsForCrossing))
        );
        
        
        hailB = new Hail(startPos, finalDir);
        int i = index+1;
        hailA = new Hail(hails.get(i).pos, subtractVectors(hails.get(i).vel, firstHail.vel));
        while(hailA.ax.compareTo(hailB.ax) == 0 || hailA.vel.x.compareTo(BigDecimal.ZERO) == 0 || hailA.vel.y.compareTo(BigDecimal.ZERO) == 0) {
            i++;
            hailA = new Hail(hails.get(i).pos, subtractVectors(hails.get(i).vel, firstHail.vel));
        }
           
        leftSide = (hailA.ax).subtract((hailB.ax));
        rightSide = (hailB.b).subtract((hailA.b));
        x = rightSide.divide(leftSide, mc);

        BigDecimal stepsForA = x.subtract((hailA.pos.x)).divide((hailA.vel.x), mc);
        BigDecimal stepsForB = x.subtract((hailB.pos.x)).divide((hailB.vel.x), mc);
            
        BigDecimal diffA = stepsForCrossing.subtract(stepsForA);
        BigDecimal diffB = stepsForCrossing.subtract(stepsForB);
        
        BigDecimal times = diffA.divide(diffB, mc);
                
        finalDir.x = (finalDir.x).divide(times, mc);
        finalDir.y = (finalDir.y).divide(times, mc);
        finalDir.z = (finalDir.z).divide(times, mc);
                        
        finalDir = addVectors(finalDir, firstHail.vel);
        hailA = hails.get(index);
        
        startPos = new Vector(
            hailA.pos.x.add(hailA.vel.x.multiply(stepsForCrossing)).subtract(finalDir.x.multiply(stepsForCrossing)),
            hailA.pos.y.add(hailA.vel.y.multiply(stepsForCrossing)).subtract(finalDir.y.multiply(stepsForCrossing)), 
            hailA.pos.z.add(hailA.vel.z.multiply(stepsForCrossing)).subtract(finalDir.z.multiply(stepsForCrossing))
        );
                
        answerPart2 = startPos.x.add(startPos.y).add(startPos.z).longValue();
        
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
        Vector pos;
        Vector vel;
        
        BigDecimal ax;
        BigDecimal b; 
        
        public Hail(Vector pos, Vector vel) {
            this.pos = pos;
            this.vel = vel;
            ax = this.vel.y.divide(this.vel.x, mc);
            BigDecimal steps = this.pos.x.divide(this.vel.x, mc);
            b = this.pos.y.subtract(this.vel.y.multiply(steps));
         }
    }
    
    public static class Vector {
        
        BigDecimal x;
        BigDecimal y;
        BigDecimal z;
        
        public Vector(BigDecimal x, BigDecimal y, BigDecimal z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        void toUnitVector() {
            BigDecimal div = gcd(this.x, gcd(this.y, this.z));

            this.x = this.x.divide(div, mc);
            this.y = this.y.divide(div, mc);
            this.z = this.z.divide(div, mc);
        }
    }
    
    public static Vector subtractVectors(Vector a, Vector b) {
        Vector c = new Vector(a.x.subtract(b.x), a.y.subtract(b.y), a.z.subtract(b.z));
        return c;
    }
    
    public static Vector addVectors(Vector a, Vector b) {
        Vector c = new Vector(a.x.add(b.x), a.y.add(b.y), a.z.add(b.z));
        return c;
    }
    
    public static Vector crossProduct(Vector a, Vector b) {
        Vector c = new Vector(
            (a.y.multiply(b.z)).subtract(a.z.multiply(b.y)),
            (a.z.multiply(b.x)).subtract(a.x.multiply(b.z)),
            (a.x.multiply(b.y)).subtract(a.y.multiply(b.x))
        );
        return c;
    }
    
    public static BigDecimal dotProduct(Vector a, Vector b) {
        BigDecimal c = (a.x.multiply(b.x)).add(a.y.multiply(b.y)).add(a.z.multiply(b.z));
        return c;
    }
    
    static BigDecimal gcd(BigDecimal a, BigDecimal b) {

        if (a.compareTo(BigDecimal.ZERO) == 0) {
            return b;
        }
       
        return gcd(b.remainder(a), a);
    }
}
