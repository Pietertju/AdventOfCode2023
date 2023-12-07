package Day07;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 *
 * @author Pieter
 */
public class Day7 {
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day7/input.txt");
        
        long answerPart1 = 0;
        long answerPart2 = 0;
                
        ArrayList<Hand> hands = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
                    
            while ((line = br.readLine()) != null) {
                String[] inputSplit = line.split(" ");
                String hand = inputSplit[0];
                long bet = Long.parseLong(inputSplit[1]);
                hands.add(new Hand(hand, bet));
            }                
        } catch(IOException e) {
            System.out.println(e.toString());
        }
        
        // part 1
        Comparator<Hand> handsComparator = Comparator.comparing(hand -> hand.getRank(false));
        for(int i = 0; i < 5; i++) {
            final int index = i;
            handsComparator = handsComparator.thenComparing(Comparator.comparing(hand -> hand.getIndex(index, false)));
        }              
        hands.sort(handsComparator);
        
        answerPart1 = countHands(hands);
        
        // part 2
        handsComparator = Comparator.comparing(hand -> hand.getRank(true));
        for(int i = 0; i < 5; i++) {
            final int index = i;
            handsComparator = handsComparator.thenComparing(Comparator.comparing(hand -> hand.getIndex(index, true)));
        }              
        hands.sort(handsComparator);
        
        answerPart2 = countHands(hands);
        
        
        long endTime = Benchmark.currentTime();
        long elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
    }
    
    public static long countHands(ArrayList<Hand> hands) {
        long multiplier = 0;
        long count = 0;
        for(Hand hand : hands) {
            multiplier++;
            count += multiplier * hand.bet;
        }
        
        return count;
    }
    
    public static class Hand {
        String hand;
        long bet;

        public Hand(String hand, long bet) {
            this.hand = hand;
            this.bet = bet;
        }
        
        public int getRank(boolean jokers) {
            String copyOfHand = this.hand;
            
            if(jokers) {
                this.hand = this.hand.replace("J","");
                if(this.hand.length() == 0) {
                    this.hand = copyOfHand;
                    return 7;
                }
            }
            
            long mostCommonCount = this.hand.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .max(comparingByValue()).get().getValue();
            
            // Add jokers to most common count
            mostCommonCount += (5-this.hand.length());
            
            char mostCommonCharacter = this.hand.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .max(comparingByValue()).get().getKey();
            
            int rank = -1;
            
            if(mostCommonCount == 5) {
                rank = 7;
            } else if(mostCommonCount == 4) {
                rank = 6;
            } else if (mostCommonCount == 3) {
                long leastCommon = this.hand.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .min(comparingByValue()).get().getValue();
                if(leastCommon == 2) rank = 5;
                else rank = 4;
            } else if (mostCommonCount == 2) {
                String handWithoutMostCommon = this.hand.replace(Character.toString(mostCommonCharacter), "");
                long secondMostCommon = handWithoutMostCommon.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .max(comparingByValue()).get().getValue();
                
                if(secondMostCommon == 2) rank = 3;
                else rank = 2;
            } else if (mostCommonCount == 1) {
                rank = 1;
            }
            
            this.hand = copyOfHand;
            return rank;
        }
        
        public int getIndex(int index, boolean joker) {
            char c = this.hand.charAt(index);
            if(Character.isDigit(c)) {
                return Character.getNumericValue(c);
            } else {
                if(c == 'A') return 14;
                if(c == 'K') return 13;
                if(c == 'Q') return 12;
                if(c == 'J') {
                    if(joker) return 1;
                    return 11;
                }
                if(c == 'T') return 10;
            }
            
            return -1;
        }
    }
}

