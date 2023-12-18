package Day13;

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
public class Day13 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day13/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        int lines = 0;
        ArrayList<Field> fields = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            
            ArrayList<String> field = new ArrayList<>();         
            while ((line = br.readLine()) != null) {
                if(!line.isEmpty()) {
                    field.add(line.trim());
                } else {
                    fields.add(new Field(field));
                    field = new ArrayList<>();
                }
            }                     
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        for(int i = 0; i < fields.size(); i++) {
            char[][] field = fields.get(i).field;
            int[] colMirror = findMirrorColumns(field);
            
            int index = 1;
            for(int faults : colMirror) {
                if(faults==0) {
                    answerPart1 += index;
                } else if(faults == 1) {
                    answerPart2 += index;
                }
                index++;
            } 
            
            
            int[] rowMirror = findMirrorRows(field);
            index = 1;
            for(int faults : rowMirror) {
                if(faults == 0) {
                    answerPart1 += index*100;
                } else if(faults == 1) {
                    answerPart2 += index*100;
                }
                index++;
            }
        }
        
        long endTime = Benchmark.currentTime();
        double elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
    
    public static class Field {
        char[][] field;
        public Field(ArrayList<String> field) {
            this.field = new char[field.size()][field.get(0).length()];
            
            for(int i = 0; i < field.size(); i++) {
                String s = field.get(i);
                this.field[i] = s.toCharArray();
            }
        }
    }
    
    public static void prettyPrint(char[][] field)  {
        for(char[] c : field) {
            for(char character : c) {
                System.out.print(character + "");
            }
            System.out.println("");
        }
    }    
    
    public static int[] findMirrorColumns(char[][] field) {
        int[] afterColumn = new int[field[0].length - 1];
        for(char[] row : field) {
            for(int j = 0; j < afterColumn.length; j++) {
                boolean possible = true;
                for(int numberOfRowsMirrored = 0; numberOfRowsMirrored <= Math.min(j, afterColumn.length-(j+1)); numberOfRowsMirrored++) {
                    //before and after is the same
                    if(row[j-numberOfRowsMirrored] != row[j+1+numberOfRowsMirrored]) {
                        possible = false;
                        break;
                    }
                }         
               if(!possible) afterColumn[j]++;
            }
        }
        
        return afterColumn;
    }
    
    public static int[] findMirrorRows(char[][] field) {
        field = transposeMatrix(field);
        return findMirrorColumns(field);
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
}