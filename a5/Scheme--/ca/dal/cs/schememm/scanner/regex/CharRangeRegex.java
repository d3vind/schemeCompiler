package ca.dal.cs.schememm.scanner.regex;

import ca.dal.cs.schememm.common.Pair;
import ca.dal.cs.schememm.scanner.Token;
import ca.dal.cs.schememm.scanner.fa.NFA;

/**
 * Represents a regular expression corresponding to a range of characters.
 */
class CharRangeRegex extends Regex {

    CharRangeRegex(Integer from, Integer to) {
        this.charRange = new Pair<>(from, to);
    }

    @Override
    public NFA nfa(Token.Name name) {
        return NFA.charRangeNFA(charRange, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CharRangeRegex)) {
            return false;
        }
        CharRangeRegex other = (CharRangeRegex) obj;
        return charRange.equals(other.charRange);
    }

    @Override
    public String toString() {
        return String.format("Chars(%d,%d)", charRange.left, charRange.right);
    }

    private final Pair<Integer, Integer> charRange;
}
