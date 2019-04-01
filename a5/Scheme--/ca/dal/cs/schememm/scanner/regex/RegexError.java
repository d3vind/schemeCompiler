package ca.dal.cs.schememm.scanner.regex;

import ca.dal.cs.schememm.common.Pair;

/**
 * RegexError class.  Indicates an error in a regular expression string.
 */
public class RegexError extends Exception {

    /**
     * Constructs a regular expression error from an unexpected character and
     * a position in the character stream.  The character may be null to
     * indicate the end of the input.
     *
     * @param ch  the unexpected character
     * @param pos the position where it was found
     */
    RegexError(Integer ch, Pair<Integer, Integer> pos) {
        this.ch = ch;
        this.pos = pos;
    }

    @Override
    public String getMessage() {
        if (ch != null) {
            return String.format(
                    "Regex syntax error: Unexpected character '%c' at position %d:%d",
                    ch, pos.left, pos.right);
        } else {
            return String.format(
                    "Regex syntax error: Unexpected end of string at position %d:%d",
                    pos.left, pos.right);
        }
    }

    private final Integer ch;
    private final Pair<Integer, Integer> pos;
}
