package ca.dal.cs.schememm.scanner.fa;

import ca.dal.cs.schememm.common.Pair;

import java.util.*;

/**
 * The base class for both NFA and DFA.  The internal representation of NFA and
 * DFA is the same.
 */
class FA {

    /**
     * Construct the automaton from its start state and list of accepting
     * states.  (The other states are represented implicitly by being reachable
     * from the start state.)
     *
     * @param start     the start state
     * @param accepting the list of accepting states
     */
    FA(State start, List<State> accepting) {
        this.start = start;
        this.accepting = accepting;
    }

    /**
     * Copy constructor.  Makes a deep copy of the given FA.
     *
     * @param other the FA to be copied
     */
    FA(FA other) {
        Set<State> toCopy = new HashSet<>();
        State.collectStates(other.start, toCopy);
        IdentityHashMap<State, State> copiedStates = new IdentityHashMap<>();
        this.accepting = new ArrayList<>();
        for (State state: toCopy) {
            State copiedState = new State(state.token);
            copiedStates.put(state, copiedState);
            if (state.token != null) {
                this.accepting.add(copiedState);
            }
        }
        this.start = copiedStates.get(other.start);
        for (State state: toCopy) {
            State copiedState = copiedStates.get(state);
            for (Pair<Pair<Integer, Integer>, State> trans : state.neighbours) {
                copiedState.addTransition(trans.left,
                                          copiedStates.get(trans.right));
            }
        }
    }

    State       start;
    List<State> accepting;

}
