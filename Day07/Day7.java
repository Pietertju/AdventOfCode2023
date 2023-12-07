
package Day07;




import Benchmarking.Benchmark;
import static Day04.Day4.contains;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import static java.util.Map.Entry.comparingByValue;
import java.util.Scanner;
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
        
        int lines = 0;
        
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
        
        hands.sort(Comparator.comparing(Hand::getRank)
                .thenComparing(Hand::get0Index)
                .thenComparing(Hand::get1Index)
                .thenComparing(Hand::get2Index)
                .thenComparing(Hand::get3Index)
                .thenComparing(Hand::get4Index)
        );
        
        //Collections.reverse(hands);
        
        int multiplier = 0;
        for(Hand hand : hands) {
            multiplier++;
            answerPart1 += multiplier * hand.bet;
            System.out.println(hand.hand + "  " + hand.getRank() + "  -  " + multiplier);
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
        char most;
        long more;
        public Hand(String hand, long bet) {
            this.hand = hand;
            this.bet = bet;
        }
        
        public int getRank() {
            String withoutJokers = this.hand.replace("J","");
            int withoutLength = withoutJokers.length();
            if(withoutLength == 0) return 7;
            long c = withoutJokers.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .max(comparingByValue()).get().getValue();
            c+= (5-withoutJokers.length());
            this.more = c;
            char character = withoutJokers.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .max(comparingByValue()).get().getKey();
            this.most = character;
            String hand2 = withoutJokers.replace(Character.toString(character), "");
            
            if(c == 5) {
                return 7;
            } else if(c==4) {
                return 6;
            } else if (c==3) {
                long d = withoutJokers.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .min(comparingByValue()).get().getValue();
                if(d == 2) {
                    return 5;
                }
                return 4;
            } else if (c == 2) {
                long d = hand2.chars().mapToObj(x -> (char) x)
                            .collect(groupingBy(x -> x, counting()))
                            .entrySet()
                            .stream()
                            .max(comparingByValue()).get().getValue();
                if(d == 2) return 3;
                return 2;
            } else if (c == 1) {
                return 1;
            } else {
                return 0;
            }
        }
        
        public int getIndex(int index) {
            char c = this.hand.charAt(index);
            if(Character.isDigit(c)) {
                return Character.getNumericValue(c);
            } else {
                if(c == 'A') return 14;
                if(c == 'K') return 13;
                if(c == 'Q') return 12;
                if(c == 'J') return 1;
                if(c == 'T') return 10;
            }
            
            return -1;
        }
        public int get0Index() {
            int index = 0;
            return getIndex(index);
        }
        public int get1Index() {
            int index = 1;
            return getIndex(index);
        }
        public int get2Index() {
            int index = 2;
            return getIndex(index);
        }public int get3Index() {
            int index = 3;
            return getIndex(index);
        }public int get4Index() {
            int index = 4;
            return getIndex(index);
        }
       

//        @Override
//        public int compareTo(Hand o){
//            return Comparator.comparing(Hand::getRank)
//                      .thenComparing(Hand::get0Index)
//                        .thenComparing(Hand::get1Index)
//                        .thenComparing(Hand::get2Index)
//                        .thenComparing(Hand::get3Index)
//                        .thenComparing(Hand::get4Index)
//                      .compare(o, this);
//        }
    }
}

