package ca.dal.cs.schememm.scanner.regex;

import ca.dal.cs.schememm.scanner.Token;
import ca.dal.cs.schememm.scanner.fa.NFA;

/**
 * Represents a regular expressino corresponding to the empty string
 */
class EpsilonRegex extends Regex {

    @Override
    public NFA nfa(Token.Name name) {
        return NFA.epsilonNFA(name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EpsilonRegex;
    }

    @Override
    public String toString() {
        return "Eps";
    }
}
