package ca.dal.cs.schememm.scanner.fa;

import ca.dal.cs.schememm.common.Pair;
import ca.dal.cs.schememm.scanner.Token;

import java.util.*;

/**
 * Class representing a state of a finite automaton.
 */
class State {

    /**
     * Create a new state accepting the given token.  If <code>token</code> is
     * <code>null</code>, then this is a non-accepting state.
     *
     * @param token the accepted token or <code>null</code>
     */
    State(Token.Name token) {
        this.token = token;
        this.neighbours = new ArrayList<>();
    }

    /**
     * Add a transition for the given character range to the given state
     *
     * @param chars     the character range that allows this transition or
     *                  <code>null</code> if this is an eps-transition.
     * @param neighbour the neighbour reached by the transition.
     */
    void addTransition(Pair<Integer, Integer> chars, State neighbour) {
        neighbours.add(new Pair<>(chars, neighbour));
    }

    /**
     * Add all states reachable from <code>state</code> in the given set.
     *
     * @param state     the state from which to start the search for reachable
     *                  states
     * @param collected the set of reachable states
     */
    static void collectStates(State state, Set<State> collected) {
        if (!collected.contains(state)) {
            collected.add(state);
            for (Pair<Pair<Integer, Integer>, State> trans : state.neighbours) {
                collectStates(trans.right, collected);
            }
        }
    }

    /**
     * Return the state this state transitions to upon reading the given
     * character.  Since this is used only for DFA, we do not support
     * eps-transitions.
     *
     * @param ch the read character
     * @return the new state
     */
    State transition(Integer ch) {
        for (Pair<Pair<Integer, Integer>, State> trans : neighbours) {
            Pair<Integer, Integer> range = trans.left;
            if (range.left <= ch && ch <= range.right) {
                return trans.right;
            }
        }
        return null;
    }

    /**
     * Check whether this is a trap state.
     *
     * @return true if and only if this is a trap state
     */
    boolean isTrap() {
        if (neighbours.size() != 1) {
            return false;
        }
        Pair<Pair<Integer, Integer>, State> nbr = neighbours.get(0);
        if (nbr.right != this) {
            return false;
        }
        return (nbr.left.left == 0 && nbr.left.right == Character.MAX_VALUE);
    }

    Token.Name                                token;
    List<Pair<Pair<Integer, Integer>, State>> neighbours;
}
