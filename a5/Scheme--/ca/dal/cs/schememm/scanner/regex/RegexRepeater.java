package ca.dal.cs.schememm.scanner.regex;

/**
 * An object that encapsulates a certain number of repetitions of an underlying
 * regular expression.
 */
class RegexRepeater {

    /**
     * Create a RegexRepeater with a lower bound and an upper bound.  A negative
     * lower bound means no lower bound.  A negative upper bound means no upper
     * bound.
     *
     * @param from the minimum number of repetitions
     * @param to   the maximum number of repetitions
     */
    RegexRepeater(int from, int to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Apply this repeater to a given regular expression.
     *
     * @param regex the regular expression to be repeated
     * @return the regular expression that repeats the inner regular expression
     */
    Regex apply(Regex regex) {
        if (from == 1 && to == 1) {
            return regex;
        } else {
            return new RepeatRegex(regex, from, to);
        }
    }

    private final int from;
    private final int to;
}
