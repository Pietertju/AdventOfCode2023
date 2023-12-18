package Day07;

import Benchmarking.Benchmark;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 *
 * @author Pieter
 */
public class Day7 {
    
    private static boolean joker = false;
    
    public static void main(String[] args) {
        long startTime = Benchmark.currentTime();
        File file = new File("res/day07/input.txt");
        
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
        Collections.sort(hands);      
        answerPart1 = countHands(hands);
        
        
        // part 2
        joker = true;
        
        Collections.sort(hands);
        answerPart2 = countHands(hands);
        
        
        long endTime = Benchmark.currentTime();
        double elapsed = Benchmark.elapsedTime(startTime, endTime);
     
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
    
    
    private static class Hand implements Comparable<Hand> {
        String hand;
        long bet;

        private Hand(String hand, long bet) {
            this.hand = hand;
            this.bet = bet;
        }
        
        private int getRank(boolean jokers) {
            var mostCommonSet = this.hand.chars().mapToObj(x -> (char) x).filter(x -> (joker) ? x != 'J' : true)
                            .collect(groupingBy(x -> x, counting()));
             
            long mostCommonCount = 0;
            long distinctCharacters = 1;
            
            if(!mostCommonSet.isEmpty()) {
                mostCommonCount = mostCommonSet.entrySet().stream().max(comparingByValue()).get().getValue();
                distinctCharacters = this.hand.chars().filter(x -> (joker) ? x != 'J' : true).distinct().count();
            }
            
            if(jokers) {
                long numJokers = this.hand.chars().filter(x -> x == 'J').count();
                mostCommonCount += numJokers;
            }
            
            
            int rank = (int) (mostCommonCount - distinctCharacters);
            return rank;
        }
        
        private int getIndex(int index, boolean joker) {
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

        @Override
        public int compareTo(Hand o) {
            int rank = this.getRank(joker) - o.getRank(joker);
            if(rank == 0) {
                int highestCard = 0;
                int index = 0;
                while(highestCard == 0) {
                    highestCard = this.getIndex(index, joker) - o.getIndex(index, joker);
                    index++;
                }
                return highestCard;
            } else {
                return rank;
            }
        }
    }
}

