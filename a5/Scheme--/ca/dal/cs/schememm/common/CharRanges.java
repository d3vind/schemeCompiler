package ca.dal.cs.schememm.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Various utility functions for manipulating character ranges
 */
public class CharRanges {

    /**
     * Given a collection of character ranges, find the smallest set of ranges
     * that do not conflict with any of the given ranges.  In other words, each
     * of the output ranges is contained in or disjoint from each of the input
     * ranges.
     */
    public static List<Pair<Integer, Integer>>
    subdivide(List<Pair<Integer, Integer>> charRanges) {
        List<Integer> endpoints =
                charRanges.stream()
                          .flatMap(rng -> Stream.of(rng.left, rng.right + 1))
                          .sorted()
                          .collect(Collectors.toList());
        List<Pair<Integer, Integer>> partition = new ArrayList<>();
        int                          last       = 0;
        for (int point : endpoints) {
            if (point > last) {
                partition.add(new Pair<>(last, point - 1));
                last = point;
            }
        }
        if (last <= (int) Character.MAX_VALUE) {
            partition.add(new Pair<>(last, (int) Character.MAX_VALUE));
        }
        return partition;
    }

    /**
     * Construct the smallest set of character ranges that covers the same set
     * of characters as the given set of character ranges
     */
    public static List<Pair<Integer, Integer>>
    simplify(List<Pair<Integer, Integer>> charRanges) {
        List<Pair<Integer, Integer>> simpleRanges = new ArrayList<>();
        int                          left         = 0;
        int                          right        = -1;
        charRanges.sort(Comparator.comparingInt(x -> x.left));
        for (Pair<Integer, Integer> charRange : charRanges) {
            if (charRange.left > right + 1) {
                if (right >= 0) {
                    simpleRanges.add(new Pair<>(left, right));
                }
                left = charRange.left;
            }
            if (charRange.right > right) {
                right = charRange.right;
            }
        }
        if (right >= 0) {
            simpleRanges.add(new Pair<>(left, right));
        }
        return simpleRanges;
    }

    /**
     * Given a character class represented as a collection of character ranges,
     * construct a new collection of character ranges representing the class's
     * negation.
     *
     * @param charRanges the collection of character ranges to be negated
     * @return the negated collection of character ranges
     */
    public static List<Pair<Integer, Integer>>
    negate(List<Pair<Integer, Integer>> charRanges) {
        List<Pair<Integer, Integer>> simpleRanges  = simplify(charRanges);
        List<Pair<Integer, Integer>> negatedRanges = new ArrayList<>();
        int                          left          = 0;
        for (Pair<Integer, Integer> rng : simpleRanges) {
            if (rng.left > left) {
                negatedRanges.add(new Pair<>(left, rng.left - 1));
            }
            left = rng.right + 1;
        }
        if (left <= Character.MAX_VALUE) {
            negatedRanges.add(new Pair<>(left, (int) Character.MAX_VALUE));
        }
        return negatedRanges;
    }
}
