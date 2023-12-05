/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Day5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Pieter
 */
public class Day5 {
    public static void main(String[] args) {
        File file = new File("res/day5/input.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            
            Mappings seedtosoil = new Mappings();
            Mappings soiltofertilizer = new Mappings();
            Mappings fertilizertowater = new Mappings();
            Mappings watertolight = new Mappings();
            Mappings lighttotemperature = new Mappings();
            Mappings temperaturetohumidity = new Mappings();
            Mappings humiditytolocation = new Mappings();
            Mappings[] mappingsList = new Mappings[]{seedtosoil, soiltofertilizer, fertilizertowater, watertolight, lighttotemperature, temperaturetohumidity, humiditytolocation};
         
            NumberRange[] seeds = new NumberRange[0];
            
            int currentMapping = -1; //0 means waiting for seedtosoil
            while ((line = br.readLine()) != null) {
                if(line.isBlank()) {
                    continue;
                }                      
                if(line.startsWith("seeds:")) {
                    String[] seedsStringArray = line.split(":")[1].trim().split(" ");
                    seeds = new NumberRange[seedsStringArray.length / 2];
                    int seedIndex = 0;
                    for(int i = 0; i < seedsStringArray.length; i++) {
                        long seedStart = Long.parseLong(seedsStringArray[i]);
                        long numOfSeeds = Long.parseLong(seedsStringArray[i+1]);
                        seeds[seedIndex] = new NumberRange(seedStart, seedStart + numOfSeeds -1);
                        seedIndex++;
                        i++;
                    }
                    continue;
                } else if(line.contains("map:")) {
                    currentMapping++;
                    continue;
                }
                
                String[] numbersString = line.trim().split(" ");
                
                long destinationStart = Long.parseLong(numbersString[0]);
                long sourceStart = Long.parseLong(numbersString[1]);
                long mapLength = Long.parseLong(numbersString[2]);
                
                NumberRange source = new NumberRange(sourceStart, sourceStart + mapLength-1);
                NumberRange destination = new NumberRange(destinationStart, destinationStart + mapLength-1);
                
                Mapping mapping = new Mapping(source, destination);              
                mappingsList[currentMapping].addMapping(mapping);
            }
            
            long lowest = Long.MAX_VALUE;
            
            ArrayList<NumberRange> locationRanges = new ArrayList<>();
            for (NumberRange seedRange : seeds) {
                ArrayList<NumberRange> ranges = new ArrayList<>();
                ranges.add(seedRange);
                for (Mappings mappingList : mappingsList) {
                    ranges = new ArrayList<>(mappingList.mapRange(ranges));
                }
                locationRanges.addAll(ranges);
            }
            
            for(NumberRange range : locationRanges) {
                if(range.start < lowest) {
                    lowest = range.start;
                }
            }
            System.out.println(lowest);
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }

    private static class NumberRange {
        long start;
        long end;
        public NumberRange(long start, long end) {
            this.start = start;
            this.end = end;
        }
        
        public boolean inRange(long number) {
            return number >= start && number <= end;
        }
    }
    
    private static class Mapping {
        NumberRange source;
        NumberRange destination;
        public Mapping(NumberRange source, NumberRange destination) {
            this.source = source;
            this.destination = destination;
        }     
        
        public long map(long number) {
            if(source.inRange(number)) {
                long nthNumber = number - source.start;
                return destination.start + nthNumber;
            }
            return number;
        }
        
        public AccountedUnaccountedPair mapRange(NumberRange range, ArrayList<NumberRange> unaccounted) {
            ArrayList<NumberRange> ranges = new ArrayList<>();
            ArrayList<NumberRange> unaccountedRanges = new ArrayList<>();
            if (range.start > this.source.end || range.end < this.source.start) {
                //no overlap range and source so list of unaccoutned stays the same
                unaccountedRanges = unaccounted;
            }
            else {
                // there is overlap so calculate the overlap
                long overlapstart = Math.max(range.start, this.source.start);
                long overlapend = Math.min(range.end, this.source.end);
                
                long mappedOverlapStart = this.map(overlapstart);
                long mappedOverlapEnd = this.map(overlapend);
                
                // add mapped range for the overlap
                ranges.add(new NumberRange(mappedOverlapStart, mappedOverlapEnd));
                
                // update unaccountedlist to account for new mapped range for overlap
                for(NumberRange numRange : unaccounted) {
                    if (numRange.start > overlapend || numRange.end < overlapstart) {
                        unaccountedRanges.add(numRange);
                    } else {
                        long newlyAccountedOverlapStart = Math.max(numRange.start, overlapstart);
                        long newlyAccountedOverlapEnd = Math.min(numRange.end, overlapend);

                        if(newlyAccountedOverlapStart > numRange.start) {
                            NumberRange beforeUnaccounted = new NumberRange(numRange.start, newlyAccountedOverlapStart - 1);
                            unaccountedRanges.add(beforeUnaccounted);
                        }
                        
                        if(numRange.end > newlyAccountedOverlapEnd) {
                            NumberRange afterUnaccounted = new NumberRange(newlyAccountedOverlapEnd+1, numRange.end);
                            unaccountedRanges.add(afterUnaccounted);
                        }
                    }
                }
            }

            return new AccountedUnaccountedPair(ranges, unaccountedRanges);
        }
    }
    
    private static class Mappings {
        ArrayList<Mapping> mappings;
        public Mappings() {
            this.mappings = new ArrayList<>();
        }
        
        public void addMapping(Mapping mapping) {
            this.mappings.add(mapping);
        }
        
        public long map(long number) {
            for (Mapping mapping : this.mappings) {
                if(mapping.map(number) >= 0) {
                    return mapping.map(number);
                }
            }
            return number;
        }
        
        public ArrayList<NumberRange> mapRange(ArrayList<NumberRange> numbers) {
            ArrayList<NumberRange> ranges = new ArrayList<>();
            for(NumberRange range : numbers) {
                ArrayList<NumberRange> unaccountedNumberRanges = new ArrayList<>();
                unaccountedNumberRanges.add(range);
                for(Mapping mapping : this.mappings) {
                    AccountedUnaccountedPair unaccounted = mapping.mapRange(range, unaccountedNumberRanges);
                    unaccountedNumberRanges = unaccounted.unaccountedRanges;
                    ranges.addAll(unaccounted.accountedRanges);
                }
                //add unaccounted
                for(NumberRange unaccountedRange : unaccountedNumberRanges) {
                    ranges.add(unaccountedRange);
                }
            }
           return ranges;
        }
    }
    
    private static class AccountedUnaccountedPair {
        ArrayList<NumberRange> accountedRanges;
        ArrayList<NumberRange> unaccountedRanges;
        public AccountedUnaccountedPair(ArrayList<NumberRange> accountedRanges, ArrayList<NumberRange> unaccountedRanges) {
             this.accountedRanges = accountedRanges;
             this.unaccountedRanges = unaccountedRanges;
        }
    }
}
