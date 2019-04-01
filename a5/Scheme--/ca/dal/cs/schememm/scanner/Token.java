package ca.dal.cs.schememm.scanner;

/**
 * Token class.  Represents a lexical token.
 */
public class Token {

    /**
     * Construct a token with the given name and the given string representation
     * @param name the name of the token
     * @param string the string representation of the token
     */
    public Token(Name name, String string) {
        this.name = name;
        this.string = string;
    }

    /**
     * The different types of token names
     */
    public enum Name {
        OPENRD,
        CLOSERD,
        OPENSQ,
        CLOSESQ,
        OPENCU,
        CLOSECU,
        NUMBER,
        BOOL,
        CHAR,
        STRING,
        IDENTIFIER,
        DEFINE,
        LAMBDA,
        QUOTE,
        IF,
        COND,
        BEGIN,
        LET,
        QUOTEMK
    }

    /**
     * This token's name
     */
    public Name id() {
        return name;
    }

    /**
     * This token's name as a string
     */
    public String name() {
        return name.toString();
    }

    /**
     * This token's parsed string
     */
    public String string() {
        return string;
    }

    // The name of this token
    final Name name;

    // The string that was identified as this token
    final String string;
}
