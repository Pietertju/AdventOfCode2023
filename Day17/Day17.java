package Day17;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author Pieter
 */
public class Day17 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day17/input.txt");
        
        long answerPart1;
        long answerPart2;
                
        ArrayList<String> input = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                input.add(line.trim());
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        // Read input into grid
        int[][] grid = new int[input.size()][input.get(0).length()];
        for(int i = 0; i < grid.length; i++) {
            grid[i] = input.get(i).chars()
                .map(c -> Character.digit(c, 10))
                .toArray();
        }
        long parseEnd = Benchmark.currentTime();
        
        //Part 1
        answerPart1 = shortestDistanceDijkstra(grid, 0, 3);
        
        long betweenTime = Benchmark.currentTime();
        
        //Part 2
        answerPart2 = shortestDistanceDijkstra(grid, 4, 10);
        
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
    
    
    public static long shortestDistanceDijkstra(int[][] grid, int min, int max) {
        Node source = new Node(new Coords(0,0), new Coords(-1,-1), 0);
        
        long shortestDistance = Integer.MAX_VALUE;
        
        source.setDistance(0);

        Set<Node> visitedNodes = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>();

        queue.add(source);
        visitedNodes.add(source);
        
        boolean found = false;        
        while (!queue.isEmpty() && !found) {            
            Node currentNode = queue.poll();
            ArrayList<Node> adjacentNodes = currentNode.getAdjacentNodes(grid, min, max);
            for (Node adjacentNode : adjacentNodes) {
                int edgeWeight = grid[adjacentNode.coords.x][adjacentNode.coords.y];
                if (!visitedNodes.contains(adjacentNode)) {     
                    int sourceDistance = currentNode.getDistance();
                    adjacentNode.setDistance(sourceDistance + edgeWeight);
                    if(adjacentNode.coords.x == grid.length-1 && adjacentNode.coords.y == grid[0].length-1) {
                        if(adjacentNode.consecutive >= min) {
                            shortestDistance = adjacentNode.getDistance();
                            found = true;
                            break;
                        }
                    }
                    queue.add(adjacentNode);
                    visitedNodes.add(adjacentNode);
                }
            }
        }

        return shortestDistance;
    }

    public static class Node implements Comparable<Node> {
    
        private Coords coords;
        private Coords previous;
        private int consecutive;
                
        private Integer distance = Integer.MAX_VALUE;

        public Node(Coords coords, Coords previous, int consecutive) {
            this.coords = coords;
            this.previous = previous;
            this.consecutive = consecutive;
        }
        
        public ArrayList<Node> getAdjacentNodes(int[][] grid, int min, int max) {
            ArrayList<Node> adjacentNodes = new ArrayList<>();
            //Hardcoded af, could generalize probs
            Coords leftDir = new Coords(coords.x, coords.y - 1);
            Coords rightDir = new Coords(coords.x, coords.y + 1);
            Coords upDir = new Coords(coords.x - 1, coords.y);
            Coords downDir = new Coords(coords.x + 1, coords.y);
            
            Node left = new Node(leftDir, coords, 1);
            Node right = new Node(rightDir, coords, 1);
            Node up =  new Node(upDir, coords, 1);
            Node down = new Node(downDir, coords, 1);
            if(!(previous.x == -1 && previous.y == -1)) {  
                if(previous.x == coords.x) {
                    if(previous.y > coords.y) {    
                        // going left
                        left.setConsecutive(consecutive+1);
                        if(left.consecutive <= max && left.coords.inBound(grid)) {
                            adjacentNodes.add(left);
                        }
                        if(consecutive < min) return adjacentNodes;
                        if(up.coords.inBound(grid)) {
                            adjacentNodes.add(up);
                        }
                        if(down.coords.inBound(grid)) {
                            adjacentNodes.add(down);
                        }
                    } else {
                        // going right
                        right.setConsecutive(consecutive+1);
                        if(right.consecutive <= max && right.coords.inBound(grid)) {
                            adjacentNodes.add(right);
                        }
                        if(consecutive < min) return adjacentNodes;
                        if(up.coords.inBound(grid)) {
                            adjacentNodes.add(up);
                        }
                        if(down.coords.inBound(grid)) {
                            adjacentNodes.add(down);
                        }
                    }
                 } else {
                    if(previous.x > coords.x) {
                        // going up
                        up.setConsecutive(consecutive+1);
                        if(up.consecutive <= max && up.coords.inBound(grid)) {
                            adjacentNodes.add(up);
                        }
                        if(consecutive < min) return adjacentNodes;
                        if(left.coords.inBound(grid)) {
                            adjacentNodes.add(left);
                        }
                        if(right.coords.inBound(grid)) {
                            adjacentNodes.add(right);
                        }               
                    } else {
                        // going down 0.o
                        down.setConsecutive(consecutive+1);
                        if(down.consecutive <= max && down.coords.inBound(grid)) {
                            adjacentNodes.add(down);
                        }
                        if(consecutive < min) return adjacentNodes;
                        if(left.coords.inBound(grid)) {
                            adjacentNodes.add(left);
                        }
                        if(right.coords.inBound(grid)) {
                            adjacentNodes.add(right);
                        }  
                    }
                }
            } else {
                adjacentNodes.add(right);
                adjacentNodes.add(down);
            }

            return adjacentNodes;
        }
        
        public Integer getDistance() {
            return distance;
        }
        
        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        public void setConsecutive(int c) {
            this.consecutive  = c;
        }
        
        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + coords.x;
            hash = hash * 31 + coords.y;
            hash = hash * 31 + previous.x;
            hash = hash * 31 + previous.y;
            hash = hash * 31 + consecutive;
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            
            Node node = (Node) obj;
            return node.coords.x == this.coords.x &&
                   node.coords.y == this.coords.y &&
                   node.consecutive == this.consecutive &&
                   node.previous.x == this.previous.x &&
                   node.previous.y == this.previous.y;
        }

        @Override
        public int compareTo(Node o) {
            return (this.getDistance() - o.getDistance());
        }
    }
    
    public static class Coords {
        int x;
        int y;
        
        public Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public boolean inBound(int[][] grid) {
            return x >= 0 && x <= grid.length-1 && y >= 0 && y <= grid[0].length - 1;
        }
    }
    
    public static void prettyPrint(int[][] grid) {
        for(int[] c : grid) {
            for(int character : c) {
                System.out.print(String.format("%03d", character) + " ");
            }
            System.out.println("");
        }
        System.out.println("------------");
    }
}
