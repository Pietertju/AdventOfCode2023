package Day17;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Pieter
 */
public class Day17 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day17/input.txt");
        
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
        
        // Read input into grid
        int[][] grid = new int[input.size()][input.get(0).length()];
        for(int i = 0; i < grid.length; i++) {
            grid[i] = input.get(i).chars()
                .map(c -> Character.digit(c, 10))
                .toArray();
        }
        long parseEnd = Benchmark.currentTime();
        
        //Part 1
        answerPart1 = calculateShortestPathFromSource(grid, 0, 3, false);
        
        long betweenTime = Benchmark.currentTime();
        
        //Part 2
        answerPart2 = calculateShortestPathFromSource(grid, 4, 10, false);
        
        long endTime = Benchmark.currentTime();
        
        long elapsedParse = Benchmark.elapsedTime(startTime, parseEnd);
        long elapsedPart1 = Benchmark.elapsedTime(parseEnd, betweenTime);
        long elapsedPart2 = Benchmark.elapsedTime(betweenTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Parsing input took: " + elapsedParse + " ms");
        System.out.println("Part 1 took: " + elapsedPart1 + " ms");
        System.out.println("Part 1 and 2 took: " + elapsedPart2 + " ms");
    }
    
    
    public static long calculateShortestPathFromSource(int[][] grid, int min, int max, boolean showOutput) {
        Node source = new Node(new Coords(0,0), new Coords(-1,-1), 0);
        
        long shortestDistance = 0;
        
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);
        boolean found = false;
        List<Node> shortestPath = new ArrayList<>();
                    
        while (!unsettledNodes.isEmpty() && !found) {            
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            
            unsettledNodes.remove(currentNode);
            
            Set<Entry<Node, Integer>> adjacentPairs = currentNode.getAdjacentNodes(grid, min, max).entrySet();
            for (Entry<Node, Integer> adjacencyPair : adjacentPairs) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    if(adjacentNode.coords.x == grid.length-1 && adjacentNode.coords.y == grid[0].length-1) {
                        if(adjacentNode.consecutive >= min) {
                            calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                            found = true;
                            shortestDistance = adjacentNode.distance;
                            shortestPath = adjacentNode.getShortestPath();
                            break;
                        } 
                    } else {
                        calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                        unsettledNodes.add(adjacentNode);
                    }
                }
            }
            settledNodes.add(currentNode);
        }
        
        if(showOutput) {
            int[][] outputGrid = new int[grid.length][grid[0].length];
            int i = 0;
            for(Node n : shortestPath) {
                outputGrid[n.coords.x][n.coords.y] = i++;
            } prettyPrint(outputGrid);
        }

        return shortestDistance;
    }
    
    private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }
    
    private static void calculateMinimumDistance(Node node, Integer edgeWeigh, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < node.getDistance()) {
            node.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            node.setShortestPath(shortestPath);
        }
    }
    
    public static class Node {
    
        private Coords coords;
        private Coords previous;
        private int consecutive;
                
        private List<Node> shortestPath = new LinkedList<>();

        private Integer distance = Integer.MAX_VALUE;

        Map<Node, Integer> adjacentNodes = new HashMap<>();

        public Node(Coords coords, Coords previous, int consecutive) {
            this.coords = coords;
            this.previous = previous;
            this.consecutive = consecutive;
        }
        
        public Map<Node, Integer> getAdjacentNodes(int[][] grid, int min, int max) {
            adjacentNodes = new HashMap<>();
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
                            adjacentNodes.put(left, grid[left.coords.x][left.coords.y]);
                        }
                        if(consecutive < min) return adjacentNodes;
                        if(up.coords.inBound(grid)) {
                            adjacentNodes.put(up, grid[up.coords.x][up.coords.y]);
                        }
                        if(down.coords.inBound(grid)) {
                            adjacentNodes.put(down, grid[down.coords.x][down.coords.y]);
                        }
                    } else {
                        // going right
                        right.setConsecutive(consecutive+1);
                        if(right.consecutive <= max && right.coords.inBound(grid)) {
                            adjacentNodes.put(right, grid[right.coords.x][right.coords.y]);
                        }
                        if(consecutive < min) return adjacentNodes;
                        if(up.coords.inBound(grid)) {
                            adjacentNodes.put(up, grid[up.coords.x][up.coords.y]);
                        }
                        if(down.coords.inBound(grid)) {
                            adjacentNodes.put(down, grid[down.coords.x][down.coords.y]);
                        }
                    }
                 } else {
                    if(previous.x > coords.x) {
                        // going up
                        up.setConsecutive(consecutive+1);
                        if(up.consecutive <= max && up.coords.inBound(grid)) {
                            adjacentNodes.put(up, grid[up.coords.x][up.coords.y]);
                        }
                        if(consecutive < min) return adjacentNodes;
                        if(left.coords.inBound(grid)) {
                            adjacentNodes.put(left, grid[left.coords.x][left.coords.y]);
                        }
                        if(right.coords.inBound(grid)) {
                            adjacentNodes.put(right, grid[right.coords.x][right.coords.y]);
                        }               
                    } else {
                        // going down 0.o
                        down.setConsecutive(consecutive+1);
                        if(down.consecutive <= max && down.coords.inBound(grid)) {
                            adjacentNodes.put(down, grid[down.coords.x][down.coords.y]);
                        }
                        if(consecutive < min) return adjacentNodes;
                        if(left.coords.inBound(grid)) {
                            adjacentNodes.put(left, grid[left.coords.x][left.coords.y]);
                        }
                        if(right.coords.inBound(grid)) {
                            adjacentNodes.put(right, grid[right.coords.x][right.coords.y]);
                        }  
                    }
                }
            } else {
                adjacentNodes.put(right, grid[right.coords.x][right.coords.y]);
                adjacentNodes.put(down, grid[down.coords.x][down.coords.y]);
            }

            return adjacentNodes;
        }
        
        public Integer getDistance() {
            return distance;
        }
        
        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        public List<Node> getShortestPath() {
            return shortestPath;
        }
        
        public void setShortestPath(LinkedList<Node> shortestPath) {
            this.shortestPath = shortestPath;
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
