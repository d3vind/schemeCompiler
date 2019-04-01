package ca.dal.cs.schememm.scanner.regex;

import ca.dal.cs.schememm.scanner.Token;
import ca.dal.cs.schememm.scanner.fa.NFA;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an alternation expr1|...|exprn between regular expressions.
 */
class AlternativeRegex extends Regex {

    AlternativeRegex(List<Regex> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public NFA nfa(Token.Name name) {
        return NFA.parallelJoin(
                alternatives.stream()
                            .map(regex -> regex.nfa(name))
                            .collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AlternativeRegex)) {
            return false;
        }
        AlternativeRegex other = (AlternativeRegex) obj;
        if (alternatives.size() != other.alternatives.size()) {
            return false;
        }
        for (int i = 0; i < alternatives.size(); ++i) {
            if (!alternatives.get(i).equals(other.alternatives.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringWriter fmt = new StringWriter();
        fmt.write("Alternative");
        formatList(fmt, alternatives);
        return fmt.toString();
    }

    private List<Regex> alternatives;
}
