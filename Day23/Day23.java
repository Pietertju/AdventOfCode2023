package Day23;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Pieter
 */
public class Day23 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day23/input.txt");
        
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
        for(int i = 0; i < grid.length; i++) {
            grid[i] = inputStrings.get(i).toCharArray();
        }
        int startingIndex = 0;
        int destinationIndex = 0;
        
        for(int i = 0; i < grid[0].length; i++) {
            if(grid[0][i] == '.') {
                startingIndex = i;
            }
            
            if(grid[grid.length-1][i] == '.') {
                destinationIndex = i;
            }
        }
        
        Node startingNode = new Node(new Coords(0, startingIndex));
        Coords destinationCoords = new Coords(grid.length-1, destinationIndex);
        
        long parseEnd = Benchmark.currentTime();
        
        //Part 1
        answerPart1 = getMaximumDistance(startingNode, grid, destinationCoords, false);
        
        long betweenTime = Benchmark.currentTime();
        
        //Part 2
        answerPart2 = getMaximumDistance(startingNode, grid, destinationCoords, true);
        
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
    
    public static long getMaximumDistance(Node startingNode, char[][] grid, Coords destinationCoords, boolean part2) {
        Deque<Node> queue = new ArrayDeque<>();
            
        long maxDistance = 0;
        
        startingNode.setDistance(0);
        queue.add(startingNode);
        int count = 0;
        while (!queue.isEmpty()) {
            count++;
            Node currentNode = queue.pollLast();
            
            if(currentNode.coords.x == destinationCoords.x && currentNode.coords.y == destinationCoords.y) {
               if(currentNode.getDistance() > maxDistance) {
                    maxDistance = currentNode.getDistance();
                    System.out.println(maxDistance);
                }
                
               continue;
            } 
            
            ArrayList<Node> adjacentNodes = currentNode.getAdjacentNodes(grid, part2);
            for (Node adjacentNode : adjacentNodes) {
                int sourceDistance = currentNode.getDistance();
                adjacentNode.setDistance(sourceDistance + 1);
                HashSet<Coords> visited = new HashSet<Coords>(currentNode.getVisited());
                visited.add(currentNode.coords);
                adjacentNode.setVisited(visited);
                queue.add(adjacentNode);
            }
        }

        return maxDistance;
    }
    
    public static class Coords {
        int x;
        int y;
        
        public Coords(int x, int y) {
            this.x = x;
            this.y = y;
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
            
            Coords node = (Coords) obj;
            return node.x == this.x &&
                   node.y == this.y;
        }
    }
    
    public static class Node {
        Coords coords;
        
        int distance = 0;
        
        HashSet<Coords> visited;
        
        public Node(Coords coords) {
            this.coords = coords;
            visited = new HashSet<Coords>();
        }
        
        public HashSet<Coords> getVisited() {
            return visited;
        }
        
        public void setVisited(HashSet<Coords> newSet) {
            visited = newSet;
        }
        
        public ArrayList<Node> getAdjacentNodes(char[][] grid, boolean part2) {
            ArrayList<Node> neighbours = new ArrayList<>();
                        
            Node left = new Node(new Coords(coords.x, coords.y-1));
            Node right = new Node(new Coords(coords.x, coords.y+1));
            Node top = new Node(new Coords(coords.x-1, coords.y));
            Node bottom = new Node(new Coords(coords.x+1, coords.y));
            
            if(!part2) {
                if(grid[coords.x][coords.y] == '>') {
                    if(!visited.contains(right.coords)) neighbours.add(right);
                    return neighbours;
                } else if(grid[coords.x][coords.y] == '<') {
                    if(!visited.contains(left.coords)) neighbours.add(left);
                    return neighbours;
                } else if(grid[coords.x][coords.y] == '^') {
                    if(!visited.contains(top.coords)) neighbours.add(top);
                    return neighbours;
                } else if(grid[coords.x][coords.y] == 'v') {
                    if(!visited.contains(bottom.coords)) neighbours.add(bottom);
                    return neighbours;
                }
            }

            if(left.isValid(grid) && !visited.contains(left.coords)) {
                neighbours.add(left);
            }  
            if(right.isValid(grid) && !visited.contains(right.coords)) {
                neighbours.add(right);
            }   
            if(top.isValid(grid) && !visited.contains(top.coords)) {
                neighbours.add(top);
            }
            if(bottom.isValid(grid) && !visited.contains(bottom.coords)) {
                neighbours.add(bottom);
            }
            return neighbours;
        }
        
        public int getDistance() {
            return this.distance;
        }
        
        public void setDistance(int distance) {
            this.distance = distance;
        }
        
        public boolean isValid(char[][] grid) {
            return ((coords.x >= 0 && coords.x < grid.length) && (coords.y >= 0 && coords.y < grid[0].length) && (grid[coords.x][coords.y] != '#'));
        }
    }
    
    public static void prettyPrintPath(HashSet<Coords> visited, char[][] grid) {
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[0].length; j++) {
                char c = grid[i][j];
                boolean contains = false;
                for(Coords n : visited) {
                    if(n.x == i && n.y == j) {
                        contains = true;
                        break;
                    }
                }
                if(contains) {
                    c = 'O';
                }
                System.out.print(c + " ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
