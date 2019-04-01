package ca.dal.cs.schememm.scanner.fa;

import ca.dal.cs.schememm.common.Pair;
import ca.dal.cs.schememm.scanner.Token;
import ca.dal.cs.schememm.scanner.regex.Regex;
import ca.dal.cs.schememm.scanner.regex.RegexError;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a non-deterministic finite automaton.
 */
public class NFA extends FA {

    /**
     * Construct an NFA From a regular expression.
     *
     * @param regex string representation of the regular expression
     * @param name  the name of the token accepted by this NFA
     * @return the constructed NFA object
     */
    public static NFA
    fromRegex(String regex, Token.Name name) throws IOException, RegexError {
        Regex re = Regex.parse(regex);
        return re.nfa(name);
    }

    /**
     * An NFA representing the empty string
     *
     * @param name the name of the token accepted by this NFA
     * @return the constructed NFA object
     */
    public static NFA epsilonNFA(Token.Name name) {
        State start = new State(name);
        return new NFA(start, Collections.singletonList(start));
    }

    /**
     * An NFA representing a one-character string with the character in a given
     * character range.
     *
     * @param charRange the range of permissible characters
     * @param name      the name of the token accepted by this NFA
     * @return the constructed NFA object
     */
    public static NFA charRangeNFA(Pair<Integer, Integer> charRange,
                                   Token.Name name) {
        State start     = new State(null);
        State accepting = new State(name);
        start.addTransition(charRange, accepting);
        return new NFA(start, Collections.singletonList(accepting));
    }

    /**
     * An NFA representing the parallel decomposition (alternative) of a list of
     * NFAs.
     *
     * @param alternatives the NFAs to be joined
     * @return the constructed NFA object
     */
    public static NFA parallelJoin(List<NFA> alternatives) {
        State start = new State(null);
        for (NFA nfa : alternatives) {
            start.addTransition(null, nfa.start);
        }
        List<State> accepting = alternatives
                .stream()
                .flatMap(nfa -> nfa.accepting.stream())
                .collect(Collectors.toList());
        return new NFA(start, accepting);
    }

    /**
     * An NFA representing the sequential decomposition (concatenation) of a
     * list of NFAs.
     *
     * @param seq the NFAs to be joined
     * @return the constructed NFA object
     */
    public static NFA sequentialJoin(List<NFA> seq) {
        State       start     = null;
        List<State> accepting = null;
        for (NFA nfa : seq) {
            if (accepting != null) {
                for (State s : accepting) {
                    s.token = null;
                    s.addTransition(null, nfa.start);
                }
                accepting = nfa.accepting;
            }
            else {
                start = nfa.start;
                accepting = nfa.accepting;
            }
        }
        return new NFA(start, accepting);
    }

    /**
     * An NFA representing an arbitrary number of repetitions (Kleene star) of
     * an inner NFA.
     *
     * @param innerNFA the NFA to be repeated
     * @param token    the token accepted by the NFA
     * @return the constructed NFA object
     */
    public static NFA kleeneStar(NFA innerNFA, Token.Name token) {
        for (State state : innerNFA.accepting) {
            state.token = null;
            state.addTransition(null, innerNFA.start);
        }
        innerNFA.start.token = token;
        return new NFA(innerNFA.start,
                       Collections.singletonList(innerNFA.start));
    }

    /**
     * Copy constructor.  Makes a deep copy of the given NFA.
     *
     * @param other the NFA to be copied
     */
    public NFA(NFA other) {
        super(other);
    }

    /**
     * Construct an NFA from its start state and its list of accepting states.
     * (Other states are represented implicitly by being reachable from the
     * start state.)
     *
     * @param start     the start state
     * @param accepting the list of accepting states
     */
    private NFA(State start, List<State> accepting) {
        super(start, accepting);
    }
}
