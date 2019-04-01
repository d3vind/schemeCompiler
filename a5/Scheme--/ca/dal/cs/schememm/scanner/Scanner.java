package ca.dal.cs.schememm.scanner;

import ca.dal.cs.schememm.common.CharStream;
import ca.dal.cs.schememm.common.Pair;
import ca.dal.cs.schememm.common.Stream;
import ca.dal.cs.schememm.scanner.fa.DFA;
import ca.dal.cs.schememm.scanner.fa.NFA;
import ca.dal.cs.schememm.scanner.regex.RegexError;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Scanner class.  Produces a stream of tokens.
 */
public class Scanner implements Stream<Token> {

    /**
     * Main function.  Runs the scanner on the given file.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("USAGE: java Scanner <scheme-- file>");
            System.exit(1);
        }
        try {
            CharStream chars  = CharStream.fromFile(args[0]);
            Scanner    tokens = new Scanner(chars);
            while (tokens.item() != null) {
                Token                  tok = tokens.item();
                Pair<Integer, Integer> pos = tokens.pos();
                System.out.printf("%s \"%s\" [%d:%d]\n",
                                  tok.name(), tok.string(),
                                  pos.left, pos.right);
                tokens.step();
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Constructor.  Constructs a scanner over the given character stream.
     *
     * @param chars the underlying character stream
     */
    public Scanner(CharStream chars) throws IOException, RegexError,
                                             LexicalError {
        this.chars = chars;
        NFA integer = NFA.fromRegex("[-+]?[0-9]+|0x[0-9a-fA-F]+|0b[01]+",
                                    Token.Name.NUMBER);
        NFA real = NFA.fromRegex("[-+]?[0-9]*\\.[0-9]+([eE][-+]?[0-9]+)" +
                                 "?|[-+]?[0-9]+[eE][-+]?[0-9]+",
                                 Token.Name.NUMBER);
        NFA bool = NFA.fromRegex("#[tf]", Token.Name.BOOL);
        NFA character =
                NFA.fromRegex("#\\\\(.|newline|space|tab|[0-3][0-7]{2})",
                              Token.Name.CHAR);
        NFA string = NFA.fromRegex(
                "\"([^\\\"]|\\\\([\\\"tn]|[0-3][0-7]{2}))*\"",
                                   Token.Name.STRING);
        NFA identifier = NFA.fromRegex(
                "[^][(){};0-9'\"# \t\r\n][^][(){};'\"# \t\r\n]*",
                                       Token.Name.IDENTIFIER);
        NFA nfa = NFA.parallelJoin(Arrays.asList(
                integer, real, bool, character, string, identifier));
        dfa = DFA.minimize(DFA.fromNFA(nfa));
        keywords = new HashMap<>();
        keywords.put("define", Token.Name.DEFINE);
        keywords.put("lambda", Token.Name.LAMBDA);
        keywords.put("quote", Token.Name.QUOTE);
        keywords.put("if", Token.Name.IF);
        keywords.put("cond", Token.Name.COND);
        keywords.put("begin", Token.Name.BEGIN);
        keywords.put("let", Token.Name.LET);
        step();
    }

    @Override
    public Token item() {
        return item;
    }

    @Override
    public Pair<Integer, Integer> pos() {
        return pos;
    }

    @Override
    public void step() throws IOException, LexicalError {
        skipWhitespace();
        pos = chars.pos();
        if (chars.item() != null) {
            switch ((int) chars.item()) {
                case '(':
                    item = new Token(Token.Name.OPENRD, "(");
                    chars.step();
                    break;
                case ')':
                    item = new Token(Token.Name.CLOSERD, ")");
                    chars.step();
                    break;
                case '[':
                    item = new Token(Token.Name.OPENSQ, "[");
                    chars.step();
                    break;
                case ']':
                    item = new Token(Token.Name.CLOSESQ, "]");
                    chars.step();
                    break;
                case '{':
                    item = new Token(Token.Name.OPENCU, "{");
                    chars.step();
                    break;
                case '}':
                    item = new Token(Token.Name.CLOSECU, "}");
                    chars.step();
                    break;
                case '\'':
                    item = new Token(Token.Name.QUOTEMK, "'");
                    chars.step();
                    break;
                default:
                    try {
                        item = dfa.run(chars);
                        if (item.name == Token.Name.IDENTIFIER) {
                            Token.Name name = keywords.get(item.string);
                            if (name != null) {
                                item = new Token(name, item.string);
                            }
                        }
                    }
                    catch (LexicalError e) {
                        throw new LexicalError(e.token, pos);
                    }
            }
        }
        else {
            item = null;
        }
    }

    /**
     * Skip over whitespace and comments.
     */
    private void skipWhitespace() throws IOException {
        while (true) {
            while (chars.item() != null &&
                   Character.isWhitespace(chars.item())) {
                chars.step();
            }
            if (chars.item() == null || chars.item() != ';') {
                break;
            }
            chars.step();
            while (chars.item() != null && chars.item() != '\n') {
                chars.step();
            }
        }
    }

    private final CharStream                  chars;
    private       Token                       item;
    private       Pair<Integer, Integer>      pos;
    private final DFA                         dfa;
    private final HashMap<String, Token.Name> keywords;
}
