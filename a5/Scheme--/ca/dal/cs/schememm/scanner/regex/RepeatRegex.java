package ca.dal.cs.schememm.scanner.regex;

import ca.dal.cs.schememm.scanner.Token;
import ca.dal.cs.schememm.scanner.fa.NFA;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A regular expression that represents a range of repetitions of an inner
 * regular expression.
 */
class RepeatRegex extends Regex {

    /**
     * Construct the repeated regular expression
     *
     * @param regex the regular expression to be repeated
     * @param from  the minimum number of repetitions (-1 = no minimum)
     * @param to    the maximum number of repetitions (-1 = no maximum)
     */
    RepeatRegex(Regex regex, int from, int to) {
        this.regex = regex;
        this.from = from > 0 ? from : 0;
        this.to = to;
    }

    @Override
    public NFA nfa(Token.Name name) {
        NFA innerNFA = regex.nfa(name);
        NFA required = requiredNFA(innerNFA);
        NFA optional = optionalNFA(innerNFA, name);
        if (required == null) {
            return optional;
        }
        else if (optional == null) {
            return required;
        }
        else {
            return NFA.sequentialJoin(Arrays.asList(required, optional));
        }
    }

    /**
     * Construct an NFA representing the minimum number of repetitions
     *
     * @param innerNFA the NFA to be repeated
     * @return an NFA representing <code>from</code> repetitions of the innerNFA
     * if <code>from > 0</code>.  <code>null</code> otherwise.
     */
    private NFA requiredNFA(NFA innerNFA) {
        if (from > 0) {
            List<NFA> seq = IntStream.range(0, from)
                                     .boxed()
                                     .map(i -> new NFA(innerNFA))
                                     .collect(Collectors.toList());
            return NFA.sequentialJoin(seq);
        }
        else {
            return null;
        }
    }

    /**
     * Construct an NFA representing the optional repetitions of the inner NFA.
     * (<code>to - max(0, from)</code> if <code>to >= 0</code>, infinite
     * otherwise)
     *
     * @param innerNFA the NFA to be repeated
     * @param name     the name of the token accepted by the NFA
     * @return an NFA representing the required repetitions of the inner NFA or
     * <code>null</code> if <code>to >= 0</code> and <code>to - max(0, from) <=
     * 0</code>.
     */
    private NFA optionalNFA(NFA innerNFA, Token.Name name) {
        if (to < 0) {
            return NFA.kleeneStar(innerNFA, name);
        }
        else if (to > 0 && to - from > 0) {
            List<NFA> seq = IntStream.range(0, to - from)
                                     .boxed()
                                     .map(i -> {
                                         NFA copy = new NFA(innerNFA);
                                         NFA eps  = NFA.epsilonNFA(name);
                                         return NFA.parallelJoin(
                                                 Arrays.asList(copy, eps));
                                     })
                                     .collect(Collectors.toList());
            return NFA.sequentialJoin(seq);
        }
        else {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RepeatRegex)) {
            return false;
        }
        RepeatRegex other = (RepeatRegex) obj;
        return from == other.from && to == other.to &&
               regex.equals(other.regex);
    }

    @Override
    public String toString() {
        return String.format("Repeat(%s,%d,%d)", regex.toString(), from, to);
    }

    private Regex regex;
    private int   from;
    private int   to;
}
