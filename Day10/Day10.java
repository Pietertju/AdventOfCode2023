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
    static final int NOT_FLOODED = 0;
    static final int PART_OF_LOOP = 1;
    static final int FLOODED_AREA = 2;
    static final int AROUND_LOOP = 3;
    
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        
        File file = new File("res/day10/input.txt");
        
        long answerPart1;
        long parityAnswerPart2;
        long floodAnswerPart2;
        
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
        
        // Replace S tile for convenience sake in parity check
        lines[startIndex.lineIndex][startIndex.charIndex] = replaceS(lines, startIndex);
       
        // Part 1 
        int[][] loopGrid = findLoop(lines, startIndex);
        
        long steps = 0;
        for(int i = 0; i < loopGrid.length; i++) {
            for(int j = 0; j < loopGrid[0].length; j++) {
                if(loopGrid[i][j] == PART_OF_LOOP) steps++;
            }
        }
        answerPart1 = steps/2;
                
        // Part 2 parity checking method
        parityAnswerPart2 = parityCheckInsideLoop(loopGrid, lines);
        
        // Part 2 flood-fill method
        int[][] floodGrid = createFloodGrid(loopGrid, lines);
        floodGrid = floodGrid(floodGrid);
        
        long interiorTiles = 0;      
        for(int i = 0; i < floodGrid.length; i++) {
            for(int j = 0; j < floodGrid[0].length; j++) {
                // After flooding, the only NOT_FLOODED tiles that remain are interior tiles;
                if(floodGrid[i][j] == NOT_FLOODED) interiorTiles++;
            }
        }    
        floodAnswerPart2 = interiorTiles / 9;
        
        long endTime = Benchmark.currentTime();
        double elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2 parity method: " + parityAnswerPart2);
        System.out.println("Part 2 flood method: " + floodAnswerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }

    
    public static int[][] floodGrid(int[][] floodGrid) {
        Queue<Index> nodes = new LinkedList<>();
        
        // Since we expanded each grid entry to a new 3 by 3 grid we are guaranteed that the 0, 0 coordinates are outside our loop
        Index startIndex = new Index(0, 0);
        floodGrid[startIndex.lineIndex][startIndex.charIndex] = FLOODED_AREA;
        
        // Add start index to the queue
        nodes.add(startIndex);
        
        // Iteratively add neighbours untill queue is empty (everything is then flooded)
        while(!nodes.isEmpty()) {
            Index currentIndex = nodes.poll();
                        

            Index nextIndex = new Index(currentIndex.lineIndex-1, currentIndex.charIndex);
            int value;
            if(inRange(nextIndex, floodGrid)) {
                value = nextIndex.getValue(floodGrid);
                if(value == NOT_FLOODED || value == AROUND_LOOP) {
                    floodGrid[nextIndex.lineIndex][nextIndex.charIndex] = FLOODED_AREA;
                    nodes.add(nextIndex);
                }
            }

            nextIndex = new Index(currentIndex.lineIndex, currentIndex.charIndex - 1);
            if(inRange(nextIndex, floodGrid)) {
                value = nextIndex.getValue(floodGrid);
                if(value == NOT_FLOODED || value == AROUND_LOOP) {
                    floodGrid[nextIndex.lineIndex][nextIndex.charIndex] = FLOODED_AREA;
                    nodes.add(nextIndex);
                }
            }

            nextIndex = new Index(currentIndex.lineIndex, currentIndex.charIndex + 1);
            if(inRange(nextIndex, floodGrid)) {
                value = nextIndex.getValue(floodGrid);
                if(value == NOT_FLOODED || value == AROUND_LOOP) {
                    floodGrid[nextIndex.lineIndex][nextIndex.charIndex] = FLOODED_AREA;
                    nodes.add(nextIndex);
                }
            }

            nextIndex = new Index(currentIndex.lineIndex+1, currentIndex.charIndex);
            if(inRange(nextIndex, floodGrid)) {
                value = nextIndex.getValue(floodGrid);
                if(value == NOT_FLOODED || value == AROUND_LOOP) {
                    floodGrid[nextIndex.lineIndex][nextIndex.charIndex] = FLOODED_AREA;
                    nodes.add(nextIndex);
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
    
    public static char replaceS(char[][] grid, Index sIndex) {
        boolean up = false, right = false, down = false, left = false;
        
        Index checkIndex = new Index(sIndex.lineIndex-1, sIndex.charIndex);
        char c;
        if(inRange(checkIndex, grid)) {
            c = checkIndex.getCharacter(grid);
            if(c == '|' || c == '7' || c == 'F') {
                up = true;
            }
        }
        checkIndex = new Index(sIndex.lineIndex, sIndex.charIndex-1);
        if(inRange(checkIndex, grid)) {
            c = checkIndex.getCharacter(grid);
            if(c == '-' || c == 'L' || c == 'F') {
                left = true;
            }
        }
        checkIndex = new Index(sIndex.lineIndex, sIndex.charIndex+1);
        if(inRange(checkIndex, grid)) {
            c = checkIndex.getCharacter(grid);
            if(c == '-' || c == '7' || c == 'J') {
                right = true;
            }
        }
        checkIndex = new Index(sIndex.lineIndex+1, sIndex.charIndex);
        if(inRange(checkIndex, grid)) {
            c = checkIndex.getCharacter(grid);
            if(c == '|' || c == 'J' || c == 'L') {
                down = true;
            }
        }
        
        if(up && down) return '|';
        else if(up && left) return 'J';
        else if(up && right) return 'L';
        else if(down && left) return '7';
        else if(down && right) return 'F';
        else if(left && right) return '-';
        return 'S';
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
            if(index.lineIndex == startIndex.lineIndex && index.charIndex == startIndex.charIndex) loopFound = true;
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
                    char c = lines[i][j];
                    if(c == '|') {
                        parity++;
                        continue;
                    }
                    
                    if(c == '-') continue;
                    
                    if(!inTurn) {
                        inTurn = true;
                        previousTurn = c;
                    } else {
                        if(c == '7' && previousTurn == 'F') {
                            // U turn downwards so no crossing
                        } else if(c == 'J' && previousTurn == 'L') {
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
        Index nextIndex;
        char c;
        if(lastChar == '|') {
            if(previousIndex.lineIndex < index.lineIndex) {             
                nextIndex = new Index(index.lineIndex + 1, index.charIndex);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '|' || c == 'J' || c == 'L') {
                        return nextIndex;
                    }
                }
            } else {
                nextIndex = new Index(index.lineIndex - 1, index.charIndex);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '|' || c == 'F' || c == '7') {
                        return nextIndex;
                    }
                }
            }        
        } else if(lastChar == '-') {
            if(previousIndex.charIndex < index.charIndex) {
                nextIndex = new Index(index.lineIndex, index.charIndex + 1);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '-' || c == 'J' || c == '7') {
                        return nextIndex;
                    }
                }
            } else {
                nextIndex = new Index(index.lineIndex, index.charIndex - 1);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '-' || c == 'F' || c == 'L') {
                        return nextIndex;
                    }
                }
            }
        } else if(lastChar == '7') {
            if(previousIndex.charIndex < index.charIndex) {             
                nextIndex = new Index(index.lineIndex + 1, index.charIndex);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '|' || c == 'J' || c == 'L') {
                        return nextIndex;
                    }
                }
            } else {
                nextIndex = new Index(index.lineIndex, index.charIndex - 1);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '-' || c == 'F' || c == 'L') {
                        return nextIndex;
                    }
                }
            }
        } else if(lastChar == 'F') {
            if(previousIndex.charIndex > index.charIndex) {
                nextIndex = new Index(index.lineIndex + 1, index.charIndex);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '|' || c == 'J' || c == 'L') {
                        return nextIndex;
                    }
                }
            } else {
                nextIndex = new Index(index.lineIndex, index.charIndex + 1);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '-' || c == '7' || c == 'J') {
                        return nextIndex;
                    }
                }
            }
        } else if(lastChar == 'J') {
            if(previousIndex.lineIndex < index.lineIndex) {
                nextIndex = new Index(index.lineIndex, index.charIndex - 1);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '-' || c == 'F' || c == 'L') {
                        return nextIndex;
                    }
                }
            } else {
                nextIndex = new Index(index.lineIndex - 1, index.charIndex);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '|' || c == '7' || c == 'F') {
                        return nextIndex;
                    }
                }
            }
        } else if(lastChar == 'L') {
            if(previousIndex.lineIndex < index.lineIndex) {
                nextIndex = new Index(index.lineIndex, index.charIndex + 1);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '-' || c == 'J' || c == '7') {
                        return nextIndex;
                    }
                }
            } else {
                nextIndex = new Index(index.lineIndex - 1, index.charIndex);
                if(inRange(nextIndex, lines)) {
                    c = nextIndex.getCharacter(lines);
                    if(c == '|' || c == '7' || c == 'F') {
                        return nextIndex;
                    }
                }
            }
        }
        
        System.out.println("ERROR" + index.getCharacter(lines));
        return index;
    }
       
    public static boolean inRange(Index index, char[][] array) {
        return (index.lineIndex >= 0 && index.lineIndex < array.length && index.charIndex >= 0 && index.charIndex < array[0].length);
    }
    
    public static boolean inRange(Index index, int[][] array) {
        return (index.lineIndex >= 0 && index.lineIndex < array.length && index.charIndex >= 0 && index.charIndex < array[0].length);
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
        
        public int getValue(int[][] grid) {
            return grid[lineIndex][charIndex];
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
