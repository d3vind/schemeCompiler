package ca.dal.cs.schememm.scanner.regex;

import ca.dal.cs.schememm.common.CharRanges;
import ca.dal.cs.schememm.common.CharStream;
import ca.dal.cs.schememm.common.Pair;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Regular expression parser.  Takes a regular expression in string form and
 * turns it into an abstract syntax tree of the regular expression.
 */
class RegexParser {

    /**
     * Construct a regular expression parser for the given regular expression.
     *
     * @param regex the string representation of the regular expression
     */
    RegexParser(String regex) throws IOException {
        this.regex = CharStream.fromString(regex);
    }

    /**
     * Run the parser to obtain an abstract syntax tree of the regular
     * expression.
     *
     * @return the root of the abstract syntax tree
     */
    Regex run() throws IOException, RegexError {
        Regex re = parseRE();
        if (regex.item() == null) {
            return re;
        }
        else {
            throw new RegexError(regex.item(), regex.pos());
        }
    }

    /**
     * Parse a regular expression, defined as an alternation of sequences.
     *
     * @return the parsed regular expression
     */
    private Regex parseRE() throws IOException, RegexError {
        List<Regex> alternatives = new ArrayList<>();
        while (regex.item() != null && ")]{}*+?".indexOf(regex.item()) < 0) {
            alternatives.add(parseSeq());
            if (regex.item() != null && regex.item() == '|') {
                regex.step();
            }
            else if (regex.item() != null && regex.item() != ')') {
                throw new RegexError(regex.item(), regex.pos());
            }
        }
        if (alternatives.isEmpty()) {
            return new EpsilonRegex();
        }
        else if (alternatives.size() == 1) {
            return alternatives.get(0);
        }
        else {
            return new AlternativeRegex(alternatives);
        }
    }

    /**
     * Parse a sequence of blocks in a regular expression.
     *
     * @return the parsed regular expression
     */
    private Regex parseSeq() throws IOException, RegexError {
        List<Regex> seq = new ArrayList<>();
        while (regex.item() != null && "]{}*+?)|".indexOf(regex.item()) < 0) {
            seq.add(parseBlock());
        }
        if (seq.isEmpty()) {
            return new EpsilonRegex();
        }
        else if (seq.size() == 1) {
            return seq.get(0);
        }
        else {
            return new SequenceRegex(seq);
        }
    }

    /**
     * Parse a block consisting of an atom followed possibly by a repetition
     * specifier.
     *
     * @return the parsed regular expression
     */
    private Regex parseBlock() throws IOException, RegexError {
        Regex         atom = parseAtom();
        RegexRepeater rep  = parseRep();
        return rep.apply(atom);
    }

    /**
     * Parse an atom in a regular expression, which can be an individual
     * character, a character class or a parenthesized subexpression.
     *
     * @return the parsed regular expression
     */
    private Regex parseAtom() throws IOException, RegexError {
        if (regex.item() == null) {
            throw new RegexError(regex.item(), regex.pos());
        }
        else if (".][{}()*+?|\\".indexOf(regex.item()) < 0) {
            Integer ch = regex.item();
            regex.step();
            return new CharRangeRegex(ch, ch);
        }
        else if (regex.item() == '\\') {
            regex.step();
            if (regex.item() != null &&
                ".][{}()*+?|\\".indexOf(regex.item()) >= 0) {
                Integer ch = regex.item();
                regex.step();
                return new CharRangeRegex(ch, ch);
            }
            else {
                throw new RegexError(regex.item(), regex.pos());
            }
        }
        else if (regex.item() == '.') {
            regex.step();
            return new CharRangeRegex(0, (int) Character.MAX_VALUE);
        }
        else if (regex.item() == '[') {
            regex.step();
            Regex chars = parseCharClass();
            if (regex.item() != null && regex.item() == ']') {
                regex.step();
                return chars;
            }
            else {
                throw new RegexError(regex.item(), regex.pos());
            }
        }
        else if (regex.item() == '(') {
            regex.step();
            Regex subre = parseRE();
            if (regex.item() != null && regex.item() == ')') {
                regex.step();
                return subre;
            }
            else {
                throw new RegexError(regex.item(), regex.pos());
            }
        }
        else {
            throw new RegexError(regex.item(), regex.pos());
        }
    }

    /**
     * Parse a repetition specifier
     *
     * @return a constructor for an appropriately repeated regular expression
     */
    private RegexRepeater parseRep() throws IOException, RegexError {
        if (regex.item() == null || "]{}?*+".indexOf(regex.item()) < 0) {
            return new RegexRepeater(1, 1);
        }
        else if (regex.item() == '?') {
            regex.step();
            return new RegexRepeater(0, 1);
        }
        else if (regex.item() == '*') {
            regex.step();
            return new RegexRepeater(0, -1);
        }
        else if (regex.item() == '+') {
            regex.step();
            return new RegexRepeater(1, -1);
        }
        else if (regex.item() == '{') {
            regex.step();
            int from = parseNumber();
            int to;
            if (regex.item() == null) {
                throw new RegexError(regex.item(), regex.pos());
            }
            else if (regex.item() == ',') {
                regex.step();
                to = parseNumber();
            }
            else if (regex.item() == '}') {
                to = from;
            }
            else {
                throw new RegexError(regex.item(), regex.pos());
            }
            if (regex.item() != null && regex.item() == '}') {
                regex.step();
                return new RegexRepeater(from, to);
            }
            else {
                throw new RegexError(regex.item(), regex.pos());
            }
        }
        else {
            throw new RegexError(regex.item(), regex.pos());
        }
    }

    /**
     * Parse a character class
     *
     * @return a regular expression representing the parsed character class (an
     * alternation of character ranges)
     */
    private Regex parseCharClass() throws IOException, RegexError {
        if (regex.item() == null) {
            throw new RegexError(regex.item(), regex.pos());
        }
        else {
            boolean negated;
            negated = regex.item() == '^';
            if (negated) {
                regex.step();
            }
            List<Pair<Integer, Integer>> charClass = parsePosCharClass();
            if (negated) {
                charClass = CharRanges.negate(charClass);
            }
            return charClassRegex(charClass);
        }
    }

    /**
     * Parse an un-negated character class.
     *
     * @return a list of character ranges
     */
    private List<Pair<Integer, Integer>>
    parsePosCharClass() throws IOException, RegexError {
        List<Pair<Integer, Integer>> charRanges = new ArrayList<>();
        charRanges.add(parseFirstCharRange());
        while (regex.item() != null && regex.item() != ']') {
            if (regex.item() == '-') {
                regex.step();
                charRanges.add(new Pair<>((int) '-', (int) '-'));
                break;
            }
            else {
                charRanges.add(parseCharRange());
            }
        }
        if (charRanges.isEmpty()) {
            throw new RegexError(regex.item(), regex.pos());
        }
        else {
            return charRanges;
        }
    }

    /**
     * Parse a single character range that is not the first character range.
     *
     * @return a pair (from, to) representing the character range
     */
    private Pair<Integer, Integer>
    parseCharRange() throws IOException, RegexError {
        if (regex.item() != null && "]-".indexOf(regex.item()) < 0) {
            Integer from = regex.item();
            regex.step();
            return parseExtend(from);
        }
        else {
            throw new RegexError(regex.item(), regex.pos());
        }
    }

    /**
     * Parse the first character range in a character class.  (This is special
     * because it can use ] as the start character of the range while ] is
     * disallowed anywhere else in the character class.)
     *
     * @return a (from, to) pair representing the character range
     */
    private Pair<Integer, Integer>
    parseFirstCharRange() throws IOException, RegexError {
        if (regex.item() != null) {
            Integer from = regex.item();
            regex.step();
            return parseExtend(from);
        }
        else {
            throw new RegexError(regex.item(), regex.pos());
        }
    }

    /**
     * Parse "-end" part of a character range.
     *
     * @param from the first character of the character range
     * @return a (from, to) pair representing the character range
     */
    private Pair<Integer, Integer>
    parseExtend(Integer from) throws IOException, RegexError {
        if (regex.item() != null && regex.item() == '-') {
            regex.step();
            if (regex.item() != null && regex.item() != ']') {
                Integer to = regex.item();
                regex.step();
                return new Pair<>(from, to);
            }
            else {
                throw new RegexError(regex.item(), regex.pos());
            }
        }
        else {
            return new Pair<>(from, from);
        }
    }

    /**
     * Construct a character class regular expression from a collection of
     * character ranges.
     *
     * @param charRanges the list of character ranges in the class
     * @return a regular expression representing the character class
     */
    private Regex
    charClassRegex(List<Pair<Integer, Integer>> charRanges)
            throws RegexError {
        if (charRanges.isEmpty()) {
            throw new RegexError(regex.item(), regex.pos());
        }
        else if (charRanges.size() == 1) {
            return new CharRangeRegex(charRanges.get(0).left,
                                      charRanges.get(0).right);
        }
        else {
            return new AlternativeRegex(
                    charRanges.stream()
                              .map(charRange ->
                                           new CharRangeRegex(
                                                   charRange.left,
                                                   charRange.right))
                              .collect(Collectors.toList()));
        }
    }

    /**
     * Parse a number
     *
     * @return the parsed integer
     */
    private int parseNumber() throws IOException {
        StringWriter digits = new StringWriter();
        while (regex.item() != null && Character.isDigit(regex.item())) {
            digits.write(regex.item());
            regex.step();
        }
        String str = digits.toString();
        if (str.length() == 0) {
            return -1;
        }
        else {
            return Integer.parseInt(str);
        }
    }

    private CharStream regex;
}
