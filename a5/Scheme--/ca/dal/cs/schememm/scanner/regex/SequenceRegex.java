package ca.dal.cs.schememm.scanner.regex;

import ca.dal.cs.schememm.scanner.Token;
import ca.dal.cs.schememm.scanner.fa.NFA;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A regular expression that represents a sequence of subexpressions
 */
class SequenceRegex extends Regex {

    /**
     * Construct a regular expression representing the sequencing of regular
     * expressions from the list of regular expressions to be sequenced.
     *
     * @param seq the list of regular expressions to be sequenced
     */
    SequenceRegex(List<Regex> seq) {
        this.seq = seq;
    }

    @Override
    public NFA nfa(Token.Name name) {
        return NFA.sequentialJoin(
                seq.stream()
                   .map(regex -> regex.nfa(name))
                   .collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SequenceRegex)) {
            return false;
        }
        SequenceRegex other = (SequenceRegex) obj;
        if (seq.size() != other.seq.size()) {
            return false;
        }
        for (int i = 0; i < seq.size(); ++i) {
            if (!seq.get(i).equals(other.seq.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringWriter fmt = new StringWriter();
        fmt.write("Sequence");
        formatList(fmt, seq);
        return fmt.toString();
    }

    private List<Regex> seq;
}
