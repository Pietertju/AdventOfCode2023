package Day08;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Pieter
 */
public class Day8 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day08/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
        
        int lines = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String input = "";
                         
            HashMap<String, Route> routing = new HashMap<>();
            ArrayList<String> startingRoutes = new ArrayList<>();
                        
            while ((line = br.readLine()) != null) {
                if(lines == 0) {
                    input = line;
                } else {
                    if(!line.isBlank()){
                        String[] split = line.split("\\=");
                        
                        String source = split[0].trim();
                        
                        String destination = split[1].trim();
                        String destLeft = destination.split(",")[0].trim().replace("(", "");
                        String destRight = destination.split(",")[1].trim().replace(")", "");
                        
                        Route route = new Route(source, destLeft, destRight);
                        routing.put(source, route);
                        
                        if(route.source.endsWith("A")) {
                            startingRoutes.add(route.source);
                        }
                    }
                }
                lines++;
            }
            
            // Part 1           
            answerPart1 = getCycleLength(input, routing, "AAA", "ZZZ");
            
            long[] cycleLengths = new long[startingRoutes.size()];
            
            int cycleIndex = 0;
            for(String startRoute : startingRoutes) {
                cycleLengths[cycleIndex] = getCycleLength(input, routing, startRoute, "Z");
                cycleIndex++;
            }
            
            answerPart2 = findLCM(cycleLengths);
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        long endTime = Benchmark.currentTime();
        double elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }

    public static long getCycleLength(String input, HashMap<String, Route> routes, String source, String endsWith) {
        Route route = routes.get(source);
        long steps = 0;
        while(!source.endsWith(endsWith)) {
            for(char c : input.toCharArray()) {
                if(c == 'L') {
                    source = route.destinationLeft;
                } else if (c == 'R') {
                    source = route.destinationRight;        
                }

                route = routes.get(source);
                steps++;

                if(source.endsWith(endsWith)) break;
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
