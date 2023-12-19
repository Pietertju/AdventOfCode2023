package Day19;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Pieter
 */
public class Day19 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day19/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
                
        HashMap<String, String> workflows = new HashMap<>();
        ArrayList<Part> parts = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean parsingParts = false;
            while ((line = br.readLine()) != null) {
                if(line.isEmpty()) {
                    parsingParts = true;
                    continue;
                }
                
                line = line.trim();
                
                if(!parsingParts) {
                    String name = line.substring(0, line.indexOf("{"));

                    String instructions = line.substring(line.indexOf("{")+1, line.indexOf("}"));
                    workflows.put(name, instructions);
                } else {
                    line = line.substring(1, line.length() - 1);
                    String[] values = line.split(",");
                    long x = Long.parseLong(values[0].substring(2));
                    long m = Long.parseLong(values[1].substring(2));
                    long a = Long.parseLong(values[2].substring(2));
                    long s = Long.parseLong(values[3].substring(2));
                    parts.add(new Part(x, m, a, s));
                }
            }                     
        } catch(IOException e) {
            System.out.println(e.toString());
        }  
        
        long parseEnd = Benchmark.currentTime();

        //Part 1
        for(Part part : parts) {
            if(part.getsAccepted(workflows)) {
                answerPart1 += part.x + part.m + part.a + part.s;
            }
        }
                
        long betweenTime = Benchmark.currentTime();
        
        //Part 2
        Range rangeX = new Range(1, 4000);
        Range rangeM = new Range(1, 4000);
        Range rangeA = new Range(1, 4000);
        Range rangeS = new Range(1, 4000);
        RangePart startPart = new RangePart(rangeX, rangeM, rangeA, rangeS, workflows.get("in"));
        Queue<RangePart> queue = new LinkedList<>();
        queue.add(startPart);
        
        while(!queue.isEmpty()) {
            RangePart part = queue.poll();
            if(part.instruction.equals("A")) {
                answerPart2 += part.combinations();
            } else if(!part.instruction.equals("R")) {
                ArrayList<RangePart> newParts = part.nextStep(workflows);
                queue.addAll(newParts);
            }
        }        
        
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
    
    public static class RangePart {
        Range x;
        Range m;
        Range a;
        Range s;
        String instruction;
        
        public RangePart(Range x, Range m, Range a, Range s, String instruction) {
            this.x = x;
            this.m = m;
            this.a = a;
            this.s = s;
            this.instruction = instruction;
        }
        
        public long combinations() {
            return (x.end-x.start+1) * (m.end-m.start+1) * (a.end-a.start+1) * (s.end-s.start+1);
        }
        
        ArrayList<RangePart> nextStep(HashMap<String, String> workflows) {
            ArrayList<RangePart> rangeParts = new ArrayList<>();
                        
            if(workflows.containsKey(instruction)) {
                instruction = workflows.get(instruction);
                rangeParts.add(this);
                return rangeParts;
            } 
            
            String[] splitInParts = instruction.split(":", 2);
            String comparison = splitInParts[0];
            
            long compareValue = Long.parseLong(comparison.substring(2));            
            String passInstruction = splitInParts[1].substring(0, splitInParts[1].indexOf(","));
            String failInstruction = splitInParts[1].substring(splitInParts[1].indexOf(",")+1);
            
            
            if(comparison.charAt(0) == 'x') {
                if(comparison.contains(">")) {
                    //Compare value with comparison
                    if(x.start > compareValue) {
                        // Fully passes
                        this.instruction = passInstruction;
                        rangeParts.add(this);
                    } else if(x.end <= compareValue) {
                        // Fully fails
                        this.instruction = failInstruction;
                        rangeParts.add(this);
                    } else {
                        // Split up range
                        // Part that doesnt pass
                        Range range1 = new Range(x.start, compareValue);
                        RangePart rp1 = new RangePart(range1, m, a, s, failInstruction);
                        rangeParts.add(rp1);
                        
                        // Part that passes
                        Range range2 = new Range(compareValue+1, x.end);
                        RangePart rp2 = new RangePart(range2, m, a, s, passInstruction);
                        rangeParts.add(rp2);
                    }
                } else {
                    //Compare value with comparison
                    if(x.start >= compareValue) {
                        // Fully fails
                        this.instruction = failInstruction;
                        rangeParts.add(this);
                    } else if(x.end < compareValue) {
                        // Fully passes
                        this.instruction = passInstruction;
                        rangeParts.add(this);
                    } else {
                        // Split up range
                        // Part that passes
                        Range range1 = new Range(x.start, compareValue-1);
                        RangePart rp1 = new RangePart(range1, m, a, s, passInstruction);
                        rangeParts.add(rp1);
                        
                        // Part that doesn't pass
                        Range range2 = new Range(compareValue, x.end);
                        RangePart rp2 = new RangePart(range2, m, a, s, failInstruction);
                        rangeParts.add(rp2);
                    }
                } 
            } else if(comparison.charAt(0) == 'm') {
                if(comparison.contains(">")) {
                    //Compare value with comparison
                    if(m.start > compareValue) {
                        // Fully passes
                        this.instruction = passInstruction;
                        rangeParts.add(this);
                    } else if(m.end <= compareValue) {
                        // Fully fails
                        this.instruction = failInstruction;
                        rangeParts.add(this);
                    } else {
                        // Split up range
                        // Part that doesnt pass
                        Range range1 = new Range(m.start, compareValue);
                        RangePart rp1 = new RangePart(x, range1, a, s, failInstruction);
                        rangeParts.add(rp1);
                        
                        // Part that passes
                        Range range2 = new Range(compareValue+1, m.end);
                        RangePart rp2 = new RangePart(x, range2, a, s, passInstruction);
                        rangeParts.add(rp2);
                    }
                } else {
                    //Compare value with comparison
                    if(m.start >= compareValue) {
                        // Fully fails
                        this.instruction = failInstruction;
                        rangeParts.add(this);
                    } else if(m.end < compareValue) {
                        // Fully passes
                        this.instruction = passInstruction;
                        rangeParts.add(this);
                    } else {
                        // Split up range
                        // Part that passes
                        Range range1 = new Range(m.start, compareValue-1);
                        RangePart rp1 = new RangePart(x, range1, a, s, passInstruction);
                        rangeParts.add(rp1);
                        
                        // Part that doesn't pass
                        Range range2 = new Range(compareValue, m.end);
                        RangePart rp2 = new RangePart(x, range2, a, s, failInstruction);
                        rangeParts.add(rp2);
                    }
                } 
            } else if(comparison.charAt(0) == 'a') {
                if(comparison.contains(">")) {
                    //Compare value with comparison
                    if(a.start > compareValue) {
                        // Fully passes
                        this.instruction = passInstruction;
                        rangeParts.add(this);
                    } else if(a.end <= compareValue) {
                        // Fully fails
                        this.instruction = failInstruction;
                        rangeParts.add(this);
                    } else {
                        // Split up range
                        // Part that doesnt pass
                        Range range1 = new Range(a.start, compareValue);
                        RangePart rp1 = new RangePart(x, m, range1, s, failInstruction);
                        rangeParts.add(rp1);
                        
                        // Part that passes
                        Range range2 = new Range(compareValue+1, a.end);
                        RangePart rp2 = new RangePart(x, m, range2, s, passInstruction);
                        rangeParts.add(rp2);
                    }
                } else {
                    //Compare value with comparison
                    if(a.start >= compareValue) {
                        // Fully fails
                        this.instruction = failInstruction;
                        rangeParts.add(this);
                    } else if(a.end < compareValue) {
                        // Fully passes
                        this.instruction = passInstruction;
                        rangeParts.add(this);
                    } else {
                        // Split up range
                        // Part that passes
                        Range range1 = new Range(a.start, compareValue-1);
                        RangePart rp1 = new RangePart(x, m, range1, s, passInstruction);
                        rangeParts.add(rp1);
                        
                        // Part that doesn't pass
                        Range range2 = new Range(compareValue, a.end);
                        RangePart rp2 = new RangePart(x, m, range2, s, failInstruction);
                        rangeParts.add(rp2);
                    }
                } 
            } else {
                if(comparison.contains(">")) {
                    //Compare value with comparison
                    if(s.start > compareValue) {
                        // Fully passes
                        this.instruction = passInstruction;
                        rangeParts.add(this);
                    } else if(s.end <= compareValue) {
                        // Fully fails
                        this.instruction = failInstruction;
                        rangeParts.add(this);
                    } else {
                        // Split up range
                        // Part that doesnt pass
                        Range range1 = new Range(s.start, compareValue);
                        RangePart rp1 = new RangePart(x, m, a, range1, failInstruction);
                        rangeParts.add(rp1);
                        
                        // Part that passes
                        Range range2 = new Range(compareValue+1, s.end);
                        RangePart rp2 = new RangePart(x, m, a, range2, passInstruction);
                        rangeParts.add(rp2);
                    }
                } else {
                    //Compare value with comparison
                    if(s.start >= compareValue) {
                        // Fully fails
                        this.instruction = failInstruction;
                        rangeParts.add(this);
                    } else if(s.end < compareValue) {
                        // Fully passes
                        this.instruction = passInstruction;
                        rangeParts.add(this);
                    } else {
                        // Split up range
                        // Part that passes
                        Range range1 = new Range(s.start, compareValue-1);
                        RangePart rp1 = new RangePart(x, m, a, range1, passInstruction);
                        rangeParts.add(rp1);
                        
                        // Part that doesn't pass
                        Range range2 = new Range(compareValue, s.end);
                        RangePart rp2 = new RangePart(x, m, a, range2, failInstruction);
                        rangeParts.add(rp2);
                    }
                } 
            }

            return rangeParts;
        }
    }
    
    public static class Range {
        long start;
        long end;
        
        public Range(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }
    
    public static class Part {
        long x;
        long m;
        long a;
        long s;
        
        public Part(long x, long m, long a, long s) {
            this.x = x;
            this.m = m;
            this.a = a;
            this.s = s;
        }
        
        public boolean getsAccepted(HashMap<String, String> workflows) {
            String instruction = workflows.get("in");
            
            while(!(instruction.equals("A") || instruction.equals("R"))) {
                instruction = this.goThroughWorkflow(instruction);
                if(workflows.containsKey(instruction)) {
                    instruction = workflows.get(instruction);
                } 
            }
            
            return instruction.equals("A");
        }
        
        public String goThroughWorkflow(String instruction) {
            String[] splitInParts = instruction.split(":", 2);
            String comparison = splitInParts[0];
            
            long value;
            
            if(comparison.charAt(0) == 'x') {
                value = this.x;
            } else if(comparison.charAt(0) == 'm') {
                value = this.m;
            } else if(comparison.charAt(0) == 'a') {
                value = this.a;
            } else {
                value = this.s;
            }
            long compareValue = Long.parseLong(comparison.substring(2));
            boolean passedRule = false;
            if(comparison.contains(">")) {
                if(value > compareValue) passedRule = true;
            } else {
                if(value < compareValue) passedRule = true;
            }
            String newInstruction;
            if(passedRule) {
                newInstruction = splitInParts[1].substring(0, splitInParts[1].indexOf(","));
            } else {
                newInstruction = splitInParts[1].substring(splitInParts[1].indexOf(",")+1);
            }
            
            return newInstruction;
        }
    }
}
