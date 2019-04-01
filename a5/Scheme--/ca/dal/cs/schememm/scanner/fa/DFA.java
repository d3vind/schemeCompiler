package ca.dal.cs.schememm.scanner.fa;

import ca.dal.cs.schememm.common.Pair;
import ca.dal.cs.schememm.common.Stream;
import ca.dal.cs.schememm.scanner.LexicalError;
import ca.dal.cs.schememm.scanner.Token;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * A deterministic finite automaton
 */
public class DFA extends FA {

    /**
     * Construct a DFA from an NFA
     *
     * @param nfa the NFA to be converted into a DFA
     * @return a DFA that accepts the same language
     */
    public static DFA fromNFA(NFA nfa) {
        return DFABuilder.run(nfa);
    }

    /**
     * Minimize a given DFA
     *
     * @param dfa the DFA to be minimized
     * @return a DFA that accepts the same language and has the minimum number
     * of states
     */
    public static DFA minimize(DFA dfa) {
        return DFAMinimizer.run(dfa);
    }

    /**
     * Run the DFA to produce the next token from the character stream
     *
     * @param chars the character stream
     * @return the accepted token
     */
    public Token run(Stream<Integer> chars) throws IOException, LexicalError {
        State        state        = start;
        StringWriter stringWriter = new StringWriter();

        while (chars.item() != null) {
            State next = state.transition(chars.item());
            if (next.isTrap()) {
                break;
            }
            stringWriter.write(chars.item());
            chars.step();
            state = next;
        }
        if (state.token != null &&
            (chars.item() == null ||
             Character.isWhitespace(chars.item()) ||
             "()[]{}".indexOf((char) chars.item().intValue()) >= 0)) {
            return new Token(state.token, stringWriter.toString());
        }
        while (chars.item() != null &&
               !Character.isWhitespace(chars.item()) &&
               "()[]{}".indexOf((char) chars.item().intValue()) < 0) {
            stringWriter.write(chars.item());
            chars.step();
        }
        throw new LexicalError(stringWriter.toString(), new Pair<>(0, 0));
    }

    /**
     * Construct a DFA from its starting state and its list of accepting states.
     * (The other states are represented implicitly by being reachable from the
     * start state.)
     *
     * @param start     the start state
     * @param accepting the list of accepting states
     */
    DFA(State start, List<State> accepting) {
        super(start, accepting);
    }
}
