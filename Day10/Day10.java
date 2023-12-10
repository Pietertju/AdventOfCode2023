package Day10;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Pieter
 */
public class Day10 {
    static char[][] lines;
    static int lineLength = 0;
    static int[][] outside;
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day10/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        int lineCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;                   
            while ((line = br.readLine()) != null) {
                lineLength = line.length();
                lineCount++;
            }                     
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        lines = new char[lineCount][lineLength];
        outside = new int[lineCount][lineLength];
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
        
        boolean loopFound = false;
        
        long steps = 0;
        Index index = new Index(currentIndex, currentChar);
        Index previousIndex = index;
        Index newIndex;
        while(!loopFound) {
            newIndex = step(index, previousIndex);
            previousIndex = index;
            index = newIndex;
            steps++;
            outside[index.lineIndex][index.charIndex] = 2;    
            if(newIndex.character == 'S') loopFound = true;
        }
        
        answerPart1 = steps/2;
        long testAns = 0;
        for(int i = 0; i < outside.length; i++) {
            char turnPiece = ' ';
            boolean inTurn = false;
            long parity = 0;
            for(int j = 0; j < outside[0].length; j++) {
                if(outside[i][j] == 2) {
                    if(lines[i][j] == '|') {
                        parity++;
                        continue;
                    }
                    
                    if(lines[i][j] == '-') continue;
                    
                    if(!inTurn) {
                        inTurn = true;
                        turnPiece = lines[i][j];
                    } else {
                        if(lines[i][j] == '7' && turnPiece == 'L') {
                            parity++;
                        }
                        else if(lines[i][j] == 'J' && turnPiece == 'F') {
                            parity++;
                        }
                        inTurn = false;
                    }
                    continue;
                }
                
                if(parity % 2 == 1) {
                    testAns++;
                }
            }
        }
        System.out.println("TESTANS: " + testAns);
        
        // Flood try sadly doesnt work ): 
        for(int i = 0; i < outside[0].length; i++) {
            if(outside[0][i] == 0) {
                fillOutside(0, i);
            }
            
            if(outside[outside.length - 1][i] == 0) {
                fillOutside(outside.length-1, i);
            }        
        }
        
        for(int i = 0; i < outside.length; i++) {
            if(outside[i][0] == 0) {
                fillOutside(i, 0);
            }
            
            if(outside[i][outside[0].length - 1] == 0) {
                fillOutside(i, outside[0].length - 1);
            }        
        }
        
        
        for(int i = 0; i < outside.length; i++) {
            for(int j = 0; j < outside[0].length; j++) {
                //System.out.print(outside[i][j] + " ");
                        
                if(outside[i][j] == 0) answerPart2++;
            }
            //System.out.println("");
        }
        
        long endTime = Benchmark.currentTime();
        long elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
    
    public static void fillOutside(int lineIndex, int charIndex)  {
        outside[lineIndex][charIndex] = 1;
        int newLineIndex = lineIndex-1;
        
        int newCharIndex = charIndex-1;
        if(inRange(newLineIndex, newCharIndex)) {
            if(outside[newLineIndex][newCharIndex] == 0) {
                fillOutside(newLineIndex, newCharIndex);
            }
        }
        newCharIndex = charIndex;
        if(inRange(newLineIndex, newCharIndex)) {
            if(outside[newLineIndex][newCharIndex] == 0) {
                fillOutside(newLineIndex, newCharIndex);
            }
        }
        newCharIndex = charIndex+1;
        if(inRange(newLineIndex, newCharIndex)) {
            if(outside[newLineIndex][newCharIndex] == 0) {
                fillOutside(newLineIndex, newCharIndex);
            }
        }
        
        
        newLineIndex = lineIndex;
        newCharIndex = charIndex-1;
        if(inRange(newLineIndex, newCharIndex)) {
            if(outside[newLineIndex][newCharIndex] == 0) {
                fillOutside(newLineIndex, newCharIndex);
            }
        }
        newCharIndex = charIndex;
        if(inRange(newLineIndex, newCharIndex)) {
            if(outside[newLineIndex][newCharIndex] == 0) {
                fillOutside(newLineIndex, newCharIndex);
            }
        }
        newCharIndex = charIndex+1;
        if(inRange(newLineIndex, newCharIndex)) {
            if(outside[newLineIndex][newCharIndex] == 0) {
                fillOutside(newLineIndex, newCharIndex);
            }
        }
        
        newLineIndex = lineIndex+1;
        newCharIndex = charIndex-1;
        if(inRange(newLineIndex, newCharIndex)) {
            if(outside[newLineIndex][newCharIndex] == 0) {
                fillOutside(newLineIndex, newCharIndex);
            }
        }
        newCharIndex = charIndex;
        if(inRange(newLineIndex, newCharIndex)) {
            if(outside[newLineIndex][newCharIndex] == 0) {
                fillOutside(newLineIndex, newCharIndex);
            }
        }
        newCharIndex = charIndex+1;
        if(inRange(newLineIndex, newCharIndex)) {
            if(outside[newLineIndex][newCharIndex] == 0) {
                fillOutside(newLineIndex, newCharIndex);
            }
        }
    }
    
    public static Index step(Index index, Index previousIndex) {
        char lastChar = index.character;
        if(lastChar == 'S') {
            int lineIndex = index.lineIndex-1;
            int charIndex = index.charIndex;
            if(inRange(lineIndex, charIndex)) {
                if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                    return new Index(lineIndex, charIndex);
                }
            }
            lineIndex = index.lineIndex + 1;
            if(inRange(lineIndex, charIndex)) {
                if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                    return new Index(lineIndex, charIndex);
                }
            }
            
            lineIndex = index.lineIndex;
            charIndex = index.charIndex-1;
            if(inRange(lineIndex, charIndex)) {
                if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                    return new Index(lineIndex, charIndex);
                }
            }
            
            charIndex = index.charIndex+1;
            if(inRange(lineIndex, charIndex)) {
                if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                    return new Index(lineIndex, charIndex);
                }
            }
        } else if(lastChar == '|') {
            if(previousIndex.lineIndex < index.lineIndex) {
                int lineIndex = index.lineIndex + 1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex-1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }        
        } else if(lastChar == '-') {
            if(previousIndex.charIndex < index.charIndex) {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex+1;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex - 1;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        } else if(lastChar == '7') {
            if(previousIndex.charIndex < index.charIndex) {
                int lineIndex = index.lineIndex+1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex - 1;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        } else if(lastChar == 'F') {
            if(previousIndex.charIndex > index.charIndex) {
                int lineIndex = index.lineIndex+1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex + 1;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == '7' ||  lines[lineIndex][charIndex] == 'J' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        } else if(lastChar == 'J') {
            if(previousIndex.lineIndex < index.lineIndex) {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex - 1;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'F' ||  lines[lineIndex][charIndex] == 'L' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex - 1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == '7' ||  lines[lineIndex][charIndex] == 'F' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        } else if(lastChar == 'L') {
            if(previousIndex.lineIndex < index.lineIndex) {
                int lineIndex = index.lineIndex;
                int charIndex = index.charIndex + 1;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '-' ||  lines[lineIndex][charIndex] == 'J' ||  lines[lineIndex][charIndex] == '7' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            } else {
                int lineIndex = index.lineIndex - 1;
                int charIndex = index.charIndex;
                if(inRange(lineIndex, charIndex)) {
                    if(lines[lineIndex][charIndex] == '|' ||  lines[lineIndex][charIndex] == '7' ||  lines[lineIndex][charIndex] == 'F' || lines[lineIndex][charIndex] == 'S') {
                        return new Index(lineIndex, charIndex);
                    }
                }
            }
        }
        System.out.println("ERROR" + index.character);
        return index;
    }
    
    public static boolean inRange(int lineIndex, int charIndex) {
        return (lineIndex >= 0 && lineIndex < lines.length && charIndex >= 0 && charIndex < lines[0].length);
    }
    
    public static class Index {
        int lineIndex;
        int charIndex;
        char character;
        public Index(int lineIndex, int charIndex) {
            this.lineIndex = lineIndex;
            this.charIndex = charIndex;
            this.character = lines[lineIndex][charIndex];
        }
        
        
    }
}
