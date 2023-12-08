package Day08;

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
public class Day8 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day8/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        int lines = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String input = "";
             
            int index = 0;
            
            ArrayList<Route> routes = new ArrayList<>();
            ArrayList<Integer> startingRoutes = new ArrayList<>();
            
            int numberOfStartingNodes = 0;
            
            while ((line = br.readLine()) != null) {
                if(lines == 0) {
                    input = line;
                } else {
                    if(!line.isBlank()){
                        if(line.startsWith("AAA")) {
                            index = lines - 2;
                        }
                        String[] split = line.split("\\=");
                        String source = split[0].trim();
                        String destination = split[1].trim();
                        String destLeft = destination.split(",")[0].trim().replace("(", "");
                        String destRight = destination.split(",")[1].trim().replace(")", "");
                        Route route = new Route(source, destLeft, destRight);
                        routes.add(route);
                        
                        if(route.source.endsWith("A")) {
                            startingRoutes.add(lines - 2);
                            numberOfStartingNodes++;
                        }
                    }
                }
                lines++;
            }
            
            // Part 1           
            answerPart1 = getCycleLength(input, routes, index, "ZZZ");
            
            long[] cycleLengths = new long[numberOfStartingNodes];
            
            int cycleIndex = 0;
            for(Integer i : startingRoutes) {
                cycleLengths[cycleIndex] = getCycleLength(input, routes, i, "Z");
                cycleIndex++;
            }
            
            answerPart2 = findLCM(cycleLengths);
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        long endTime = Benchmark.currentTime();
        long elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
    
    public static int getRoute(ArrayList<Route> routes, String location) {
        int index = 0;
        for(Route route : routes) {
            if(route.source.equals(location)) {
                return index;
            }
            index++;
        }
        
        return 0;
    }
    
    public static long getCycleLength(String input, ArrayList<Route> routes, int index, String endsWith) {
        Route route = routes.get(index);
        String location = route.source;
        long steps = 0;
        while(!location.endsWith(endsWith)) {
            for(char c : input.toCharArray()) {
                if(c == 'L') {
                    location = route.destinationLeft;
                } else if (c == 'R') {
                    location = route.destinationRight;        
                }

                index = getRoute(routes, location);
                route = routes.get(index);
                location = route.source;
                steps++;

                if(location.equals("ZZZS")) break;
            }
        }

        return steps;
    }
    // Function to calculate the Greatest Common Divisor (GCD) for longs
    private static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    // Function to calculate the Least Common Multiple (LCM) for longs
    private static long lcm(long a, long b) {
        return Math.abs(a * b) / gcd(a, b);
    }

    // Function to find the LCM of an array of longs
    public static long findLCM(long[] array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Array must not be empty");
        }

        long result = array[0];
        for (int i = 1; i < array.length; i++) {
            result = lcm(result, array[i]);
        }

        return result;
    }
    
    public static class Route {
        String source;
        String destinationLeft;
        String destinationRight;
        public Route(String source, String destinationLeft, String destinationRight) {
            this.source = source;
            this.destinationLeft = destinationLeft;
            this.destinationRight = destinationRight;
        }
    }
}
