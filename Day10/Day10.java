package Day10;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Pieter
 */
public class Day10 {
    public static final int NOT_FLOODED = 0;
    public static final int PART_OF_LOOP = 1;
    public static final int FLOODED_AREA = 2;
    public static final int AROUND_LOOP = 3;
    
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        
        File file = new File("res/day10/input.txt");
        
        long answerPart1 = 0;
        long parityAnswerPart2 = 0;
        long floodAnswerPart2 = 0;
        
        FileDimensions fileDim = getFileLength(file);
        int lineCount = fileDim.lineCount; 
        int lineLength = fileDim.lineLength;
        
        char[][] lines = new char[lineCount][lineLength];     
        
        int currentIndex = 0;
        int currentChar = 0;
        
        lineCount = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;                   
            while ((line = br.readLine()) != null) {           
                lines[lineCount] = line.toCharArray();
                if(line.contains("S")) {
                    currentIndex = lineCount;
                    currentChar = line.indexOf('S');
                }
                lineCount++;                
            }                     
        } catch(IOException e) {
            System.out.println(e.toString());
        }
                
        Index startIndex = new Index(currentIndex, currentChar);
        
        // Part 1
        int[][] loopGrid = findLoop(lines, startIndex);
        long steps = 0;
        for(int i = 0; i < loopGrid.length; i++) {
            for(int j = 0; j < loopGrid[0].length; j++) {
                if(loopGrid[i][j] == 1) steps++;
            }
        }
        answerPart1 = steps/2;

                
        // hard coded, TODO make general based on first and previous index
        lines[startIndex.lineIndex][startIndex.charIndex] = 'F';

        // Part 2 parity checking method
        parityAnswerPart2 = parityCheckInsideLoop(loopGrid, lines);
        
        // Part 2 flood-fill method
        int[][] floodGrid = createFloodGrid(loopGrid, lines);
        floodGrid = floodGrid(floodGrid);
        
        long interiorTiles = 0;
        
        // After flooding, the only NOT_FLOODED tiles that remain are interior tiles;
        for(int i = 0; i < floodGrid.length; i++) {
            for(int j = 0; j < floodGrid[0].length; j++) {
                if(floodGrid[i][j] == NOT_FLOODED) interiorTiles++;
            }
        }
        
        floodAnswerPart2 = interiorTiles / 9;
        
        long endTime = Benchmark.currentTime();
        long elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2 parity method: " + parityAnswerPart2);
        System.out.println("Part 2 flood method: " + floodAnswerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }

    
    public static int[][] floodGrid(int[][] floodGrid) {
        Queue<Index> nodes = new LinkedList<Index>();
        
        // Since we expanded each grid entry to a new 3 by 3 grid we are guaranteed that the 0, 0 coordinates are outside our loop
        Index startIndex = new Index(0, 0);
        floodGrid[startIndex.lineIndex][startIndex.charIndex] = FLOODED_AREA;
        
        // Add start index to the queue
        nodes.add(startIndex);
        
        // Iteratively add neighbours untill queue is empty (everything is then flooded)
        while(!nodes.isEmpty()) {
            Index currentIndex = nodes.poll();
                        
            int newLineIndex = currentIndex.lineIndex-1;
            int newCharIndex = currentIndex.charIndex;
            if(inRange(newLineIndex, newCharIndex, floodGrid)) {
                if(floodGrid[newLineIndex][newCharIndex] == NOT_FLOODED || floodGrid[newLineIndex][newCharIndex] == AROUND_LOOP) {
                    Index newIndex = new Index(newLineIndex, newCharIndex);
                    floodGrid[newIndex.lineIndex][newIndex.charIndex] = FLOODED_AREA;
                    nodes.add(newIndex);
                }
            }

            newLineIndex = currentIndex.lineIndex;
            newCharIndex = currentIndex.charIndex - 1;
            if(inRange(newLineIndex, newCharIndex, floodGrid)) {
                if(floodGrid[newLineIndex][newCharIndex] == NOT_FLOODED || floodGrid[newLineIndex][newCharIndex] == AROUND_LOOP) {
                    Index newIndex = new Index(newLineIndex, newCharIndex);
                    floodGrid[newIndex.lineIndex][newIndex.charIndex] = FLOODED_AREA;
                    nodes.add(newIndex);
                }
            }

            newLineIndex = currentIndex.lineIndex;
            newCharIndex = currentIndex.charIndex + 1;
            if(inRange(newLineIndex, newCharIndex, floodGrid)) {
                if(floodGrid[newLineIndex][newCharIndex] == NOT_FLOODED || floodGrid[newLineIndex][newCharIndex] == AROUND_LOOP) {
                    Index newIndex = new Index(newLineIndex, newCharIndex);
                    floodGrid[newIndex.lineIndex][newIndex.charIndex] = FLOODED_AREA;
                    nodes.add(newIndex);
                }
            }

            newLineIndex = currentIndex.lineIndex  + 1;
            newCharIndex = currentIndex.charIndex;
            if(inRange(newLineIndex, newCharIndex, floodGrid)) {
                if(floodGrid[newLineIndex][newCharIndex] == NOT_FLOODED || floodGrid[newLineIndex][newCharIndex] == AROUND_LOOP) {
                    Index newIndex = new Index(newLineIndex, newCharIndex);
                    floodGrid[newIndex.lineIndex][newIndex.charIndex] = FLOODED_AREA;
                    nodes.add(newIndex);
                }
            }
        }
   
        return floodGrid;
    }
    
    public static int[][] createFloodGrid(int[][] loopGrid, char[][] lines) {
        int[][] floodGrid = new int[loopGrid.length*3][loopGrid[0].length*3];

        for(int i = 0; i < loopGrid.length; i++) {
            for(int j = 0; j < loopGrid[0].length; j++) {
                // 0 - 1, 1 - 4, 2 - 7
                int centralLineIndex = 1 + (i*3);
                int centralCharIndex = 1 + (j*3);
                if(loopGrid[i][j] == PART_OF_LOOP) {
                    for(int k = -1; k <= 1; k++) {
                        for(int l = -1; l <= 1; l++) {                           
                            floodGrid[centralLineIndex+k][centralCharIndex+l] = AROUND_LOOP;
                        }
                    }
                    if(lines[i][j] == '|') {
                        floodGrid[centralLineIndex-1][centralCharIndex] = PART_OF_LOOP;
                        floodGrid[centralLineIndex][centralCharIndex] = PART_OF_LOOP;
                        floodGrid[centralLineIndex+1][centralCharIndex] = PART_OF_LOOP;
                        
                    } else if(lines[i][j] == '-') {
                        floodGrid[centralLineIndex][centralCharIndex-1] = PART_OF_LOOP;
                        floodGrid[centralLineIndex][centralCharIndex] = PART_OF_LOOP;
                        floodGrid[centralLineIndex][centralCharIndex+1] = PART_OF_LOOP;
                        
                    } else if(lines[i][j] == 'F') {
                        floodGrid[centralLineIndex][centralCharIndex+1] = PART_OF_LOOP;
                        floodGrid[centralLineIndex][centralCharIndex] = PART_OF_LOOP;
                        floodGrid[centralLineIndex+1][centralCharIndex] = PART_OF_LOOP;
                    } else if(lines[i][j] == '7') {
                        floodGrid[centralLineIndex][centralCharIndex-1] = PART_OF_LOOP;
                        floodGrid[centralLineIndex][centralCharIndex] = PART_OF_LOOP;
                        floodGrid[centralLineIndex+1][centralCharIndex] = PART_OF_LOOP;
                    } else if(lines[i][j] == 'J') {
                        floodGrid[centralLineIndex][centralCharIndex-1] = PART_OF_LOOP;
                        floodGrid[centralLineIndex][centralCharIndex] = PART_OF_LOOP;
                        floodGrid[centralLineIndex-1][centralCharIndex] = PART_OF_LOOP;
                    } else if(lines[i][j] == 'L') {
                        floodGrid[centralLineIndex][centralCharIndex+1] = PART_OF_LOOP;
                        floodGrid[centralLineIndex][centralCharIndex] = PART_OF_LOOP;
                        floodGrid[centralLineIndex-1][centralCharIndex] = PART_OF_LOOP;
                    }
                }
            }
        }
        return floodGrid;
    }
      
    public static int[][] findLoop(char[][] lines, Index startIndex) {
        int[][] loopGrid = new int[lines.length][lines[0].length];
        
        boolean loopFound = false;
        
        Index index = startIndex;
        Index previousIndex = startIndex;
        Index newIndex;
        
        while(!loopFound) {
            newIndex = step(lines, index, previousIndex);
            previousIndex = index;
            index = newIndex;
            
            loopGrid[index.lineIndex][index.charIndex] = PART_OF_LOOP;    
            if(index.getCharacter(lines) == 'S') loopFound = true;
        }
        
        return loopGrid;
    } 
    
    public static long parityCheckInsideLoop(int[][] loopGrid, char[][] lines) {
        long insideCount = 0;
        for(int i = 0; i < loopGrid.length; i++) {
            char previousTurn = ' ';
            boolean inTurn = false;
            long parity = 0;
            for(int j = 0; j < loopGrid[0].length; j++) {
                if(loopGrid[i][j] == PART_OF_LOOP) {
                    if(lines[i][j] == '|') {
                        parity++;
                        continue;
                    }
                    
                    if(lines[i][j] == '-') continue;
                    
                    if(!inTurn) {
                        inTurn = true;
                        previousTurn = lines[i][j];
                    } else {
                        if(lines[i][j] == '7' && previousTurn == 'F') {
                            // U turn downwards so no crossing
                        } else if(lines[i][j] == 'J' && previousTurn == 'L') {
                            // U turn no crossing
                        } else {
                            parity++;
                        }
                        inTurn = false;
                    }
                    continue;
                }
                
                if(parity % 2 == 1) {
                    insideCount++;
                }
            }
        }
        
        return insideCount;
    }
               
    public static Index step(char[][] lines, Index index, Index previousIndex) {
        char lastChar = index.getCharacter(lines);
        if(lastChar == 'S') {
            int lineIndex = index.lineIndex-1;
            int charIndex = index.charIndex;
            if(inRange(lineIndex, charIndex, lines)) {
                if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                    return new Index(lineIndex, charIndex);
                }
            }
            lineIndex = index.lineIndex + 1;
            if(inRange(lineIndex, charIndex, lines)) {
                if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                    return new Index(lineIndex, charIndex);
                }
            }
            
            lineIndex = index.lineIndex;
            charIndex = index.charIndex-1;
            if(inRange(lineIndex, charIndex, lines)) {
                if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                    return new Index(lineIndex, charIndex);
                }
            }
            
            charIndex = index.charIndex+1;
            if(inRange(lineIndex, charIndex, lines)) {
                if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                    return new Index(lineIndex, charIndex);
                }
            }
        } else if(lastChar == '|') {
            if(previousIndex.lineIndex < index.lineIndex) {
                int lineIndex = index.lineIndex + 1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex-1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }        
        } else if(lastChar == '-') {
            if(previousIndex.charIndex < index.charIndex) {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex+1;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex - 1;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        } else if(lastChar == '7') {
            if(previousIndex.charIndex < index.charIndex) {
                int lineIndex = index.lineIndex+1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex - 1;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        } else if(lastChar == 'F') {
            if(previousIndex.charIndex > index.charIndex) {
                int lineIndex = index.lineIndex+1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex + 1;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == '7' ||  lines[lineIndex][charIndex] == 'J' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        } else if(lastChar == 'J') {
            if(previousIndex.lineIndex < index.lineIndex) {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex - 1;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex - 1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == '7' ||  lines[lineIndex][charIndex] == 'F' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        } else if(lastChar == 'L') {
            if(previousIndex.lineIndex < index.lineIndex) {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex + 1;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex - 1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex, lines)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == '7' ||  lines[lineIndex][charIndex] == 'F' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        }
        
        System.out.println("ERROR" + index.getCharacter(lines));
        return index;
    }
    
    public static boolean inRange(int lineIndex, int charIndex, char[][] array) {
        return (lineIndex >= 0 && lineIndex < array.length && charIndex >= 0 && charIndex < array[0].length);
    }
    
    public static boolean inRange(int lineIndex, int charIndex, int[][] array) {
        return (lineIndex >= 0 && lineIndex < array.length && charIndex >= 0 && charIndex < array[0].length);
    }
    
    public static class Index {
        int lineIndex;
        int charIndex;
        
        public Index(int lineIndex, int charIndex) {
            this.lineIndex = lineIndex;
            this.charIndex = charIndex;
        }
        
        public char getCharacter(char[][] lines) {
            return lines[lineIndex][charIndex];
        }
    }
    
    public static FileDimensions getFileLength(File file) {
        int lineCount = 0;
        int lineLength = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;                   
            while ((line = br.readLine()) != null) {
                lineLength = line.length();
                lineCount++;
            }                     
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        return new FileDimensions(lineCount, lineLength);
    }
    
    public static class FileDimensions {
        int lineCount;
        int lineLength;
        public FileDimensions(int lineCount, int lineLength) {
            this.lineCount = lineCount;
            this.lineLength = lineLength;
        }
    }
}