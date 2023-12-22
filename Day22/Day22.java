package Day22;

import Benchmarking.Benchmark;
import Day21.Day21;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Pieter
 */
public class Day22 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day22/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        ArrayList<Brick> bricks = new ArrayList<>();
        
        int maxX = 0;
        int maxY = 0;
                
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] split = line.split("~");
                
                String first = split[0];
                String second = split[1];
                
                String[] firstCoordString = first.split(",");
                String[] secondCoordString = second.split(",");
                
                Coords firstCoords = new Coords(Integer.parseInt(firstCoordString[0]), 
                                                Integer.parseInt(firstCoordString[1]), 
                                                Integer.parseInt(firstCoordString[2]));
                
                Coords secondCoords = new Coords(Integer.parseInt(secondCoordString[0]), 
                                                Integer.parseInt(secondCoordString[1]), 
                                                Integer.parseInt(secondCoordString[2]));
                
                if(firstCoords.z < secondCoords.z || firstCoords.y < secondCoords.y || firstCoords.x < secondCoords.x) {
                    Brick brick = new Brick(firstCoords, secondCoords);
                    bricks.add(brick);
                } else {
                    Brick brick = new Brick(secondCoords, firstCoords);
                    bricks.add(brick);
                }
                
                int brickMaxX = Math.max(firstCoords.x, secondCoords.x);
                if(maxX < brickMaxX) {
                    maxX = brickMaxX;
                }
                
                int brickMaxY = Math.max(firstCoords.y, secondCoords.y);
                if(maxY < brickMaxY) {
                    maxY = brickMaxY;
                }
                
                lineNumber++;
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        Collections.sort(bricks, (Brick b1, Brick b2) -> {
            if(b1.first.z < b2.first.z) return -1;
            if(b1.first.z > b2.first.z) return 1;
            return 0;
        });
        
        long parseEnd = Benchmark.currentTime();
        
        int[][] tower = new int[maxX+1][maxY+1];
        HashMap<Integer, ArrayList<Brick>> zMap = new HashMap<>();
        for(Brick brick : bricks) {
            int height = brick.fallBrick(tower);
            for(int x = brick.first.x; x <= brick.second.x; x++) {
                for(int y = brick.first.y; y <= brick.second.y; y++) {
                    tower[x][y] = height;
                }
            }
            if(zMap.containsKey(height)) {
                zMap.get(height).add(brick);
            } else {
                zMap.put(height, new ArrayList<>());
                zMap.get(height).add(brick);
            }
        }
        for(Brick brick : bricks) {
            int height = brick.first.z;
            if(zMap.containsKey(height-1)) {
                for(Brick b : zMap.get(height-1)) {
                    brick.supportCheck(b);      
                }
            }
        }

        //Part 1
        for(Brick brick : bricks) {
            if(!brick.supports.isEmpty()) {
                boolean mustSupport = false;
                for(Brick b : brick.supports) {
                    if(b.supportedBy.size() == 1) {
                        mustSupport = true;
                        break;
                    }
                }
                if(!mustSupport) answerPart1++;
            } else {
                answerPart1++;
            }
        }
        
        long betweenTime = Benchmark.currentTime();
        
        //Part 2
        for(Brick brick : bricks) {
            long falling = brick.fallingAfterDisintegration();
            answerPart2 += falling;
        }
        
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
    
    public static class Brick {
        Coords first;
        Coords second;
                
        ArrayList<Brick> supports;
        ArrayList<Brick> supportedBy;
        
        public Brick(Coords first, Coords second) {
            this.first = first;
            this.second = second;
            supports = new ArrayList<>();
            supportedBy = new ArrayList<>();
        }
        
        public void supportCheck(Brick b) {
           if((b.first.x <= this.second.x && this.first.x <= b.second.x) && (b.first.y <= this.second.y && this.first.y <= b.second.y)) {
               this.supportedBy.add(b);
               b.supports.add(this);
           }
        }
        
        public long fallingAfterDisintegration() {
            HashSet<Brick> disintegrated = new HashSet<>();
            disintegrated.add(this);
            
            return this.disintegrate(disintegrated);
        }
        
        public long disintegrate(HashSet<Brick> disintegrated) {
            long fallingBlocks = 0;
            
            for(Brick b : this.supports) {
                int numberOfSupporters = b.supportedBy.size();
                for(Brick b2 : b.supportedBy) {
                    if(disintegrated.contains(b2)) {
                        numberOfSupporters--;
                    }
                }
                if(numberOfSupporters == 0) {
                    disintegrated.add(b);
                    fallingBlocks++;
                    fallingBlocks += b.disintegrate(disintegrated);
                }
            }
            
            return fallingBlocks;
        }
        
        public int fallBrick(int[][] tower) {
            int maxZ = 0;
            for(int x = first.x; x <= second.x; x++) {
                for(int y = first.y; y <= second.y; y++) {
                    if(tower[x][y] > maxZ) {
                        maxZ = tower[x][y];
                    }
                }
            }
            
            int height = maxZ + 1;
            second.z = height + (second.z - first.z);
            first.z = height;
            return second.z;
        }
        
        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + first.x;
            hash = hash * 31 + second.x;
            hash = hash * 31 + first.y;
            hash = hash * 31 + second.y;
            hash = hash * 31 + first.z;
            hash = hash * 31 + second.z;
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            
            Brick brick = (Brick) obj;
            return brick.first.x == this.first.x && brick.second.x == this.second.x &&
                   brick.first.y == this.first.y && brick.second.y == this.second.y &&
                   brick.first.z == this.first.z && brick.second.z == this.second.z;
        }
    }
    
    public static class Coords {
        int x;
        int y;
        int z;
        
        public Coords(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
