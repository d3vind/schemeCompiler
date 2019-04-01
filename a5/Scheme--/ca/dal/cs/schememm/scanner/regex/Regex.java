package ca.dal.cs.schememm.scanner.regex;

import ca.dal.cs.schememm.scanner.Token;
import ca.dal.cs.schememm.scanner.fa.NFA;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * Abstract base class for all types of regular expressions.
 */
public abstract class Regex {

    /**
     * Parse a string into a regular expression.
     *
     * @param regex the string description of the regular expression
     * @return the constructed regular expression object
     */
    public static Regex parse(String regex) throws IOException, RegexError {
        RegexParser parser = new RegexParser(regex);
        return parser.run();
    }

    /**
     * An NFA that accepts the language described by this regular expression.
     *
     * @param name the name of the token represented by this regular expression
     * @return the NFA
     */
    public abstract NFA nfa(Token.Name name);

    /**
     * Format a sequence of subexpressions to a string writer
     * @param writer the string writer to use
     * @param subexps the list of subexpressions to be formatted
     */
    static void formatList(StringWriter writer, List<Regex> subexps) {
        char sep = '(';
        for (Regex subexp : subexps) {
            writer.write(sep);
            sep = ',';
            writer.write(subexp.toString());
        }
        writer.write(')');
    }

}
