package Day18;

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
public class Day18 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day18/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
                
        ArrayList<Instruction> instructions = new ArrayList<>();
        ArrayList<Instruction> instructionsPart2 = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] splitLine = line.split(" ");
                char direction = splitLine[0].charAt(0);
                long distance = Integer.parseInt(splitLine[1]);
                String color = splitLine[2].substring(2,splitLine[2].length()-1);
                instructions.add(new Instruction(direction, distance));
                
                char directionPart2 = color.charAt(color.length()-1);
                
                if(directionPart2 == '0') {
                    directionPart2 = 'R';
                } else if(directionPart2 == '1') {
                    directionPart2 = 'D';
                } else if(directionPart2 == '2') {
                    directionPart2 = 'L';
                } else {
                    directionPart2 = 'U';
                }
                
                long distancePart2 = Long.parseLong(color.substring(0, color.length()-1), 16);

                instructionsPart2.add(new Instruction(directionPart2, distancePart2));
            }    
        } catch(IOException e) {
            System.out.println(e.toString());
        }  
        
        //Part 1
        long parseEnd = Benchmark.currentTime();
        
        answerPart1 = computeArea(computeGraph(instructions));
                
        long betweenTime = Benchmark.currentTime();
        
        //Part 2
        answerPart2 = computeArea(computeGraph(instructionsPart2));
        
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
    
    public static Node[] computeGraph(ArrayList<Instruction> instructions) {
        Node[] nodes = new Node[instructions.size()+1];
       
        nodes[0] = new Node(0, 0, null);
        for(int i = 1; i < nodes.length; i++) {
            Node previousNode = nodes[i-1];
            Instruction instruction = instructions.get(i-1);
                        
            Node node;
            if(instruction.direction == 'R') {
                node = new Node(previousNode.x, previousNode.y + instruction.distance, nodes[i-1]);
            } else if(instruction.direction == 'L') {
                node = new Node(previousNode.x, previousNode.y - instruction.distance, nodes[i-1]);
            } else if(instruction.direction == 'D') {
                node = new Node(previousNode.x + instruction.distance, previousNode.y, nodes[i-1]);
            } else {
                node = new Node(previousNode.x - instruction.distance, previousNode.y, nodes[i-1]);
            }
            nodes[i] = node;
            nodes[i-1].setNext(nodes[i]);
        }
        nodes[0].setPrevious(nodes[nodes.length-1]);
        nodes[nodes.length-1].setNext(nodes[0]);
        
        return nodes;
    }
    
    // Compute area of graph using the shoelace formula
    public static long computeArea(Node[] nodes) {
        long sum1 = 0;
        long sum2 = 0;
        long perimeter = 0;
        for(int i = 0; i < nodes.length; i++) {
            Node current = nodes[i];
            Node previous;
            if(i-1 < 0) {
                previous = nodes[nodes.length-1];
            } else {
                previous = nodes[i-1];
                perimeter += Math.abs(current.x - previous.x) + Math.abs(current.y - previous.y);
            }   
            sum1 += previous.y * current.x;
            sum2 += current.y * previous.x;      
        }

        return ((Math.abs(sum1 - sum2) / 2) + ((perimeter)/2) + 1);
    }
    
    public static class Instruction {
        char direction;
        long distance;
        
        public Instruction(char direction, long distance) {
            this.direction = direction;
            this.distance = distance;
        }
    }
    
    public static class Node {
        long x;
        long y;
        Node next;
        Node previous;
        
        public Node(long x, long y, Node previous) {
            this.x = x;
            this.y = y;
            this.previous = previous;
        }
        
        void setNext(Node node) {
            this.next = node;
        }
        
        void setPrevious(Node node) {
            this.previous = node;
        }
    }
}
