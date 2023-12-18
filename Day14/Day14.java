package Day14;

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
public class Day14 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day14/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
               
        ArrayList<String> input = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                input.add(line.trim());
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        char[][] grid = new char[input.size()][input.get(0).length()];
        for(int i = 0; i < grid.length; i++) {
            grid[i] = input.get(i).toCharArray();
        }
        
        //Part 1
        answerPart1 = countLoad(fallNorth(grid));
        
        //part 2
        long numberOfCycles = 1000000000;
        ArrayList<char[][]> grids = new ArrayList<>();
        
        int cycleLength = -1;
        int cycleOffset = -1;
        
        int cycles = 0;
        boolean cycleFound = false;
        while(!cycleFound) {
            grid = cycle(grid);
            for(int i = 0; i < grids.size(); i++) {
                if(gridsEqual(grids.get(i), grid)) {
                    cycleLength = cycles - i;
                    cycleOffset = i;
                    cycleFound = true;
                    break;
                }
            }
            grids.add(grid);
            cycles++;
        }
        
        numberOfCycles -= cycleOffset+1;
        int offsetToCycle = (int) (numberOfCycles % cycleLength);
        char[][] solutionGrid = grids.get(cycleOffset+offsetToCycle);
        
        answerPart2 = countLoad(solutionGrid);
        
        long endTime = Benchmark.currentTime();
        double elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
    
    public static boolean gridsEqual(char[][] grid1, char[][] grid2) {
        for(int i = 0; i < grid1.length; i++) {
            for(int j = 0; j < grid1[0].length; j++) {
                if(grid1[i][j] != grid2[i][j]) return false;
            }
        }
        
        return true;
    }
    
    public static long countLoad(char[][] grid) {
        long score = 0;
        for(int row = 0; row < grid.length; row++)  {
            for(char c : grid[row]) {
                if(c == 'O') score += grid.length - row;
            }
        }
        return score;
    }
    
    public static char[][] transposeMatrix(char[][] matrix){
        int m = matrix.length;
        int n = matrix[0].length;

        char[][] transposedMatrix = new char[n][m];

        for(int x = 0; x < n; x++) {
            for(int y = 0; y < m; y++) {
                transposedMatrix[x][y] = matrix[y][x];
            }
        }

        return transposedMatrix;
    }
    
    public static char[][] flipMatrix(char[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;

        char[][] transposedMatrix = new char[n][m];

        for(int y = 0; y < m; y++) {
            transposedMatrix[y] = matrix[(n-1)-y];
        }

        return transposedMatrix;
    }
    
    public static char[][] fallNorth(char[][] grid) {
        grid = transposeMatrix(grid); 
        grid = fallLeft(grid);
        grid = transposeMatrix(grid);
        return grid;
    }
    
    public static char[][] cycle(char[][] grid) {
        //fall north
        grid = transposeMatrix(grid); 
        grid = fallLeft(grid);
        grid = transposeMatrix(grid);

        //fall west     
        grid = fallLeft(grid);
       
        //fall south
        grid = flipMatrix(grid);
        grid = transposeMatrix(grid);
        grid = fallLeft(grid);
        grid = flipMatrix(grid);
        grid = transposeMatrix(grid);
        grid = flipMatrix(grid);
        
        //fall east
        grid = fallLeft(grid);
        grid = transposeMatrix(grid);
        grid = flipMatrix(grid);
        grid = transposeMatrix(grid);
        return grid;
    }
    
    public static char[][] fallLeft(char[][] grid) {
        for(int i = 0; i < grid.length; i++) {
            char[] row = grid[i];
            int rocksAtWall = 0;
            int lastCube = -1;
            for(int j = 0; j < row.length; j++) {
                char c = row[j];
                if(c == '.') {
                    continue;
                } else if(c == 'O') {
                    int endIndex = lastCube+1 + rocksAtWall;
                    grid[i][j] = '.';
                    grid[i][endIndex] = 'O';
                    rocksAtWall++;
                } else if (c == '#') {
                    lastCube = j;
                    rocksAtWall = 0;
                }
            }
        }
        return grid;
    }
    
    public static void prettyPrint(char[][] grid) {
        for(char[] c : grid) {
            for(char character : c) {
                System.out.print(character + "");
            }
            System.out.println("");
        }
        System.out.println("------------");
    }
}