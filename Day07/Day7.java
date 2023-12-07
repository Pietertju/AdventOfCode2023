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
        hands.sort(Comparator.comparing((Hand hand) -> hand.getRank(false))
                .thenComparing(hand -> hand.getIndex(0, false))
                .thenComparing(hand -> hand.getIndex(1, false))
                .thenComparing(hand -> hand.getIndex(2, false))
                .thenComparing(hand -> hand.getIndex(3, false))
                .thenComparing(hand -> hand.getIndex(4, false))
        );
        
        
        int multiplier = 0;
        for(Hand hand : hands) {
            multiplier++;
            answerPart1 += multiplier * hand.bet;
            //System.out.println(hand.hand + "  " + hand.getRank(true) + "  -  " + multiplier);
        }
        
        // part 2
        hands.sort(Comparator.comparing((Hand hand) -> hand.getRank(true))
                .thenComparing(hand -> hand.getIndex(0, true))
                .thenComparing(hand -> hand.getIndex(1, true))
                .thenComparing(hand -> hand.getIndex(2, true))
                .thenComparing(hand -> hand.getIndex(3, true))
                .thenComparing(hand -> hand.getIndex(4, true))
        );
        
        
        multiplier = 0;
        for(Hand hand : hands) {
            multiplier++;
            answerPart2 += multiplier * hand.bet;
        }
        
        long endTime = Benchmark.currentTime();
        long elapsed = Benchmark.elapsedTime(startTime, endTime);
     
        System.out.println("Part 1: " + answerPart1);
        System.out.println("Part 2: " + answerPart2);
        System.out.println("Part 1 and 2 took: " + elapsed + " ms combined");
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
                int withoutLength = this.hand.length();
                if(withoutLength == 0) {
                    this.hand = copyOfHand;
                    return 7;
                }
            }
            
            long c = this.hand.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .max(comparingByValue()).get().getValue();
            c += (5-this.hand.length());

            char character = this.hand.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .max(comparingByValue()).get().getKey();

            String hand2 = this.hand.replace(Character.toString(character), "");
            
            int rank = -1;
            
            if(c == 5) {
                rank = 7;
            } else if(c==4) {
                rank = 6;
            } else if (c==3) {
                long d = this.hand.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .min(comparingByValue()).get().getValue();
                if(d == 2) rank = 5;
                else rank = 4;
            } else if (c == 2) {
                long d = hand2.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .max(comparingByValue()).get().getValue();
                if(d == 2) rank = 3;
                else rank = 2;
            } else if (c == 1) {
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

