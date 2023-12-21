package Day21;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;

/**
 *
 * @author Pieter
 */
public class Day21 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day21/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        ArrayList<String> inputStrings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                inputStrings.add(line);
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        char[][] grid = new char[inputStrings.size()][inputStrings.get(0).length()];
        Node startingNode = new Node(-1, -1);
        
        for(int i = 0; i < grid.length; i++) {
            String line = inputStrings.get(i);
            grid[i] = line.toCharArray();
            int startingIndex = line.indexOf("S");
            if(startingIndex > 0) {
                startingNode = new Node(i, startingIndex);
            }
        }
        
        
        long parseEnd = Benchmark.currentTime();
        
        //Part 1
        long maxSteps = 64;
        boolean evenNumberOfSteps = maxSteps % 2 == 0;
        
        long[][] distances = shortestDistanceDijkstra(startingNode, grid, maxSteps, 2);
        answerPart1 = getPossiblePositionsGrid(distances, evenNumberOfSteps);
        
        long betweenTime = Benchmark.currentTime();
        
        //Part 2        
        maxSteps = 26501365;
        
        evenNumberOfSteps = maxSteps % 2 == 0;
        
        distances = shortestDistanceDijkstra(startingNode, grid, maxSteps, 2);
   
        long evenGridLocations = getPossiblePositionsGrid(distances, evenNumberOfSteps);
        long oddGridLocations = getPossiblePositionsGrid(distances, !evenNumberOfSteps);
        
        long stepsToFinishGrid = movesForFullGrid(distances);
        
        long stepsToReachNewGridCenter = grid.length;
        long gridsFilledFully = 1 + ((maxSteps-stepsToFinishGrid) / (stepsToReachNewGridCenter));
        
        long totalOddsFilledFully;
        long totalEvenFilledFully;
        
        long partlyFilledGrids = gridsFilledFully - 1;
        
        if(gridsFilledFully % 2 == 0) {
            totalOddsFilledFully = gridsFilledFully * gridsFilledFully;
            totalEvenFilledFully = (gridsFilledFully-1) * (gridsFilledFully-1);
        } else {
            totalEvenFilledFully = gridsFilledFully*gridsFilledFully;
            totalOddsFilledFully = (gridsFilledFully-1) * (gridsFilledFully-1);
        }
        
        long positionsFromFullGrids = (totalEvenFilledFully * evenGridLocations) + (totalOddsFilledFully * oddGridLocations);
        
        long stepsToLeftEdge = startingNode.y;
        long stepsToRightEdge = (grid[0].length - stepsToLeftEdge) - 1;
        long stepsToTopEdge = startingNode.x;
        long stepsToBottomEdge = (grid.length - stepsToTopEdge) - 1;
        
        //Cardinal directions
        long stepsForLeftGrid = maxSteps - (stepsToLeftEdge + (grid[0].length * partlyFilledGrids) + 1);
        Node leftGridStartNode = new Node(startingNode.x, grid[0].length - 1);
        distances = shortestDistanceDijkstra(leftGridStartNode, grid, stepsForLeftGrid, maxSteps - stepsForLeftGrid);
        long leftGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps);
        
        long stepsForRightGrid = maxSteps - (stepsToRightEdge + (grid[0].length * partlyFilledGrids) + 1);
        Node rightGridStartNode = new Node(startingNode.x, 0);
        distances = shortestDistanceDijkstra(rightGridStartNode, grid, stepsForRightGrid, maxSteps - stepsForRightGrid);
        long rightGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps);
        
        long stepsForTopGrid = maxSteps - (stepsToTopEdge + (grid.length * partlyFilledGrids) + 1);
        Node topStartingNode = new Node(grid.length - 1, startingNode.y);
        distances = shortestDistanceDijkstra(topStartingNode, grid, stepsForTopGrid, maxSteps - stepsForTopGrid);
        long topGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps);
        
        long stepsForBottomGrid = maxSteps - (stepsToBottomEdge + (grid.length * partlyFilledGrids) + 1);
        Node bottomStartingNode = new Node(0, startingNode.y);
        distances = shortestDistanceDijkstra(bottomStartingNode, grid, stepsForBottomGrid, maxSteps - stepsForBottomGrid);
        long bottomGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps);
        
        //Diagonal directions
        long stepsForTopRight = maxSteps - ((stepsToTopEdge + stepsToRightEdge) + (grid.length * (partlyFilledGrids-1)) + 2);
        Node topRightStartingNode = new Node(grid.length-1, 0);
        distances = shortestDistanceDijkstra(topRightStartingNode, grid, stepsForTopRight, maxSteps - stepsForTopRight);
        long topRightGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps) * partlyFilledGrids;
        
        long stepsForTopLeft = maxSteps - ((stepsToTopEdge + stepsToLeftEdge) + (grid.length * (partlyFilledGrids-1)) + 2);
        Node topLeftStartingNode = new Node(grid.length - 1, grid[0].length - 1);
        distances = shortestDistanceDijkstra(topLeftStartingNode, grid, stepsForTopLeft, maxSteps - stepsForTopLeft);
        long topLeftGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps) * partlyFilledGrids;
        
        long stepsForBottomLeft = maxSteps - ((stepsToBottomEdge + stepsToLeftEdge) + (grid.length * (partlyFilledGrids-1)) + 2);
        Node bottomLeftStartingNode = new Node(0, grid[0].length - 1);
        distances = shortestDistanceDijkstra(bottomLeftStartingNode, grid, stepsForBottomLeft, maxSteps - stepsForBottomLeft);
        long bottomLeftGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps) * partlyFilledGrids;
        
        long stepsForBottomRight = maxSteps - ((stepsToBottomEdge + stepsToRightEdge) + (grid.length * (partlyFilledGrids-1)) + 2);
        Node bottomRightStartingNode = new Node(0, 0);
        distances = shortestDistanceDijkstra(bottomRightStartingNode, grid, stepsForBottomRight, maxSteps - stepsForBottomRight);
        long bottomRightGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps) * partlyFilledGrids;
        
        // Extra grids above the diagonal ones
        long stepsForTopRightExtra = maxSteps - ((stepsToTopEdge + stepsToRightEdge + 2) + (grid.length * partlyFilledGrids));
        Node topRightExtraStartingNode = topRightStartingNode;
        distances = shortestDistanceDijkstra(topRightExtraStartingNode, grid, stepsForTopRightExtra, maxSteps - stepsForTopRightExtra);
        long topRightExtraGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps) * gridsFilledFully;
        
        long stepsForTopLeftExtra = maxSteps - ((stepsToTopEdge + stepsToLeftEdge + 2) + (grid.length * partlyFilledGrids));
        Node topLeftExtraStartingNode = topLeftStartingNode;
        distances = shortestDistanceDijkstra(topLeftExtraStartingNode, grid, stepsForTopLeftExtra, maxSteps - stepsForTopLeftExtra);
        long topLeftExtraGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps) * gridsFilledFully;
        
        long stepsForBottomLeftExtra = maxSteps - ((stepsToBottomEdge + stepsToLeftEdge + 2) + (grid.length * partlyFilledGrids));
        Node bottomLeftExtraStartingNode = bottomLeftStartingNode;
        distances = shortestDistanceDijkstra(bottomLeftExtraStartingNode, grid, stepsForBottomLeftExtra, maxSteps - stepsForBottomLeftExtra);
        long bottomLeftExtraGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps) * gridsFilledFully;
        
        long stepsForBottomRightExtra = maxSteps - ((stepsToBottomEdge + stepsToRightEdge + 2) + (grid.length * partlyFilledGrids));
        Node bottomRightExtraStartingNode = bottomRightStartingNode;
        distances = shortestDistanceDijkstra(bottomRightExtraStartingNode, grid, stepsForBottomRightExtra, maxSteps - stepsForBottomRightExtra);
        long bottomRightExtraGridPositions = getPossiblePositionsGrid(distances, evenNumberOfSteps) * gridsFilledFully;
                
        answerPart2 = positionsFromFullGrids 
                + leftGridPositions + rightGridPositions + topGridPositions + bottomGridPositions 
                + topRightGridPositions + topLeftGridPositions + bottomLeftGridPositions + bottomRightGridPositions
                + topRightExtraGridPositions + topLeftExtraGridPositions + bottomRightExtraGridPositions + bottomLeftExtraGridPositions;
        
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
    
    public static long movesForFullGrid(long[][] distances) {
        long maxDistance = 0;
    
        for(int i = 0; i < distances.length; i++) {
            for(int j = 0; j < distances[0].length; j++) {
                if(distances[i][j] > maxDistance) maxDistance = distances[i][j];
            }
        }
        
        return maxDistance-2;
    }
    
    public static long getPossiblePositionsGrid(long[][] distances, boolean evenSteps) {
        long possibleLocations = 0;
        for(int i = 0; i < distances.length; i++) {
            for(int j = 0; j < distances[0].length; j++) {
                long distance = distances[i][j];
                if(distance > 0) {
                    if((distance % 2 == 0) == evenSteps) {
                        possibleLocations++;
                    }
                }
            }
        }
        
        return possibleLocations;
    }
    
    public static long[][] shortestDistanceDijkstra(Node startingNode, char[][] grid, long maxSteps, long startingDistance) {
        LinkedHashSet<Node> visitedNodes = new LinkedHashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>();
        
        long[][] distances = new long[grid.length][grid[0].length];        
        
        startingNode.setDistance(startingDistance);
        distances[startingNode.x][startingNode.y] = startingNode.getDistance();
            
        queue.add(startingNode);
        visitedNodes.add(startingNode);
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            if(currentNode.getDistance() >= startingDistance + maxSteps) break;
            ArrayList<Node> adjacentNodes = currentNode.getAdjacentNodes(grid);
            for (Node adjacentNode : adjacentNodes) {
                if (!visitedNodes.contains(adjacentNode)) {
                    long sourceDistance = currentNode.getDistance();
                    if(sourceDistance + 1 < adjacentNode.getDistance()) {
                        adjacentNode.setDistance(sourceDistance + 1);
                        distances[adjacentNode.x][adjacentNode.y] = adjacentNode.getDistance();
                        queue.add(adjacentNode);
                        visitedNodes.add(adjacentNode);
                    }
                }
            }
        }
        return distances;
    }
    
    
    
    public static class Node implements Comparable<Node>{
        int x;
        int y;
        
        long distance = Long.MAX_VALUE;
        
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public long getDistance() {
            return distance;
        }
        
        public void setDistance(long distance) {
            this.distance = distance;
        }
        
        ArrayList<Node> getAdjacentNodes(char[][] grid) {
            ArrayList<Node> neighbours = new ArrayList<>();
            Node left = new Node(x, y-1);
            Node right = new Node(x, y+1);
            Node up = new Node(x-1, y);
            Node down = new Node(x+1, y);
            if(left.inBound(grid)) {
                if(grid[left.x][left.y] != '#') {
                    neighbours.add(left);
                }
            }
            if(right.inBound(grid)) {
                if(grid[right.x][right.y] != '#') {
                    neighbours.add(right);
                }
            }
            if(up.inBound(grid)) {
                if(grid[up.x][up.y] != '#') {
                    neighbours.add(up);
                }
            }
            if(down.inBound(grid)) {
                if(grid[down.x][down.y] != '#') {
                    neighbours.add(down);
                }
            }
            
            return neighbours;
        }
        
        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + this.x;
            hash = hash * 31 + this.y;
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            
            Node node = (Node) obj;
            return node.x == this.x &&
                   node.y == this.y;
        }

        @Override
        public int compareTo(Node o) {
            if(o.distance > this.distance) return -1;
            if(o.distance < this.distance) return 1;
            return 0;            
        }
        
        public boolean inBound(char[][] grid) {
            return ((this.x >= 0 && this.x <= grid.length - 1) && (this.y >= 0 && this.y <= grid[0].length - 1));
        }
    }
    
    static void prettyPrint(long[][] grid) {
        for(long[] line : grid) {
            for(long l : line) {
                System.out.print(String.format("%04d", l) + " ");
            } System.out.println("");
        }
    }
}
