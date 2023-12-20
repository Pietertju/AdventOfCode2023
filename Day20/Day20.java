package Day20;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author Pieter
 */
public class Day20 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day20/input.txt");
        
        long answerPart1;
        long answerPart2;
                        
        HashMap<String, Module> modules = new HashMap<>();
        ArrayList<Module> conjunctions = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] splitLine = line.split("->");
                String[] destinations = splitLine[1].trim().split(", ");
                
                char operation = splitLine[0].charAt(0);
                String name = splitLine[0].trim().substring(1);
                
                Module module;
                
                if(operation == '%') {
                    module = new FlipFlopModule(name, destinations);
                } else if(operation == '&') {
                    module = new ConjunctionModule(name, destinations);
                } else {
                    name = "broadcaster";
                    module = new BroadcastModule(destinations);
                }
                modules.put(name, module);
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }

        String finalModuleName = "";
        String finalModuleSource = "";
        for(Module module : modules.values()) {
            for(String destination : module.destinationModules) {
                if(!modules.containsKey(destination)) {
                    finalModuleName = destination;
                    finalModuleSource = module.name;
                } else {
                    modules.get(destination).addInputModule(module.name);  
                }      
            }
        }
        
        for(Module module : modules.values()) {
            for(String destination : module.destinationModules) {
                if(destination.equals(finalModuleSource)) {
                    conjunctions.add(module);
                }      
            }
        }
                
        FinalModule finalModule = new FinalModule();
        modules.put(finalModuleName, finalModule);
        
        long parseEnd = Benchmark.currentTime();
        
        //Part 1
        Pulse startPulse = new Pulse("button", false, "broadcaster");
        Queue<Pulse> queue = new LinkedList<>();
        
        queue.add(startPulse);
        
        long totalLowPulses = 0;
        long totalHighPulses = 0;
        
        boolean cyclesFound = false;
        long counter = 1;
        
        LinkedHashMap<String, Long> cycleDetector = new LinkedHashMap<>();
        LinkedHashMap<String, Boolean> cyclesDetected = new LinkedHashMap<>();

        while(!cyclesFound) {
            long lowPulses = 0;
            long highPulses = 0;
            while(!queue.isEmpty()) {
                Pulse pulse = queue.poll();
                queue.addAll(modules.get(pulse.destination).processPulse(pulse));
                if(pulse.destination.equals(finalModuleSource) && pulse.high) {
                    if(!cycleDetector.containsKey(pulse.source)) {
                        cycleDetector.put(pulse.source, counter);
                    } else {
                        cycleDetector.put(pulse.source, counter - cycleDetector.get(pulse.source));
                        cyclesDetected.put(pulse.source, true);
                    }
                }
                if(pulse.high) highPulses++;
                else lowPulses++;
            }
            
            if(counter <= 1000) {
                totalLowPulses += lowPulses;
                totalHighPulses += highPulses;
            }
                    
            counter++; 
            if(cyclesDetected.values().size() == conjunctions.size()) {
                cyclesFound = true;
            }
            queue.add(startPulse);
        }
        
        answerPart1 = totalLowPulses * totalHighPulses;
        
        
        //Part 2
        boolean first = true;
        long commonCycleLength = 0;
        for(long cycleLength : cycleDetector.values()) {
            if(first) {
                first = false;
                commonCycleLength = cycleLength;
            } else {
                commonCycleLength = lcm(commonCycleLength, cycleLength);
            }            
        }
        
        answerPart2 = commonCycleLength;
        
        long endTime = Benchmark.currentTime();
        
        double elapsedParse = Benchmark.elapsedTime(startTime, parseEnd);
        double elapsed = Benchmark.elapsedTime(parseEnd, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Parsing input took: " + elapsedParse + " ms");
        System.out.println("Parts 1 and 2 took: " + elapsed + " ms combined");
    }
    
    public static class Pulse {
        String source;
        boolean high;
        String destination;
        
        public Pulse(String source, boolean high, String destination) {
            this.source = source;
            this.high = high;
            this.destination = destination;
        }
    }
    
    public static abstract class Module {
        String[] destinationModules;
        String name;
        
        abstract ArrayList<Pulse> processPulse(Pulse pulse); 
        
        public Module(String name, String[] destinationModules) {
            this.name = name;
            this.destinationModules = destinationModules;
        }
        
        public void addInputModule(String inputModule) {}
        
        public ArrayList<Pulse> sendPulses(boolean high) {
            ArrayList<Pulse> pulses = new ArrayList<>();
            for(String destination : destinationModules) {
                pulses.add(new Pulse(this.name, high, destination));
            }
            
            return pulses;
        }
    }
    
    public static class FlipFlopModule extends Module {

        boolean on = false;
        
        public FlipFlopModule(String name, String[] destinationModules) {
            super(name, destinationModules);
        }
        
        @Override
        ArrayList<Pulse> processPulse(Pulse pulse) {            
            if(!pulse.high){
                this.on = !this.on;
                return super.sendPulses(on);      
            }
            
            return new ArrayList<>();
        }   
    }
    
    public static class ConjunctionModule extends Module {
        Map<String, Boolean> inputMemory;
        
        public ConjunctionModule(String name, String[] destinationModules) {
            super(name, destinationModules);
            inputMemory = new LinkedHashMap<>();
        }
        
        @Override
        ArrayList<Pulse> processPulse(Pulse pulse) {
            this.inputMemory.put(pulse.source, pulse.high);
            
            boolean allHighs = true;
            for(Boolean high : inputMemory.values()) {
                if(!high) {
                    allHighs = false;
                    break;
                }
            }
            
            boolean sendPulse = !allHighs;
            
            return super.sendPulses(sendPulse);
        }  
        
        @Override
        public void addInputModule(String inputModule) {
            this.inputMemory.put(inputModule, false);
        }
        
    }
    
    public static class BroadcastModule extends Module {

        public BroadcastModule(String[] destinationModules) {
            super("broadcaster", destinationModules);
        }
        
        @Override
        ArrayList<Pulse> processPulse(Pulse pulse) {            
            return super.sendPulses(pulse.high);   
        }   
    }
    
    public static class FinalModule extends Module {
        
        public FinalModule() {
            super("kill", new String[0]);
        }

        @Override
        ArrayList<Pulse> processPulse(Pulse pulse) {
            if(!pulse.high) {
                System.out.println("DAMN");
            }
            return new ArrayList<>();
        }
    }
    
    private static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    private static long lcm(long a, long b) {
        return Math.abs(a * b) / gcd(a, b);
    }
}
