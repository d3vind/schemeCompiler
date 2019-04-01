package ca.dal.cs.schememm.scanner;

import ca.dal.cs.schememm.common.Pair;

/**
 * Class representing a scanner error
 */
public class LexicalError extends Exception {

    /**
     * Construct a lexical error from the given incorrect token string and the
     * given position.
     *
     * @param token the token that was not recognized
     * @param pos   the position where the token was found
     */
    public LexicalError(String token, Pair<Integer, Integer> pos) {
        this.token = token;
        this.pos = pos;
    }

    @Override
    public String getMessage() {
        return String.format("Unexpected token '%s' at positon %d:%d",
                             token, pos.left, pos.right);
    }

    String token;
    private Pair<Integer, Integer> pos;
}
