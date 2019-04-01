package ca.dal.cs.schememm.scanner.fa;

import ca.dal.cs.schememm.common.CharRanges;
import ca.dal.cs.schememm.common.Pair;
import ca.dal.cs.schememm.scanner.Token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements the construction of a DFA from an NFA.
 */
class DFABuilder {

    /**
     * Run a DFA builder on a given NFA.
     *
     * @param nfa the NFA to be converted to a DFA
     * @return the constructed DFA
     */
    static DFA run(NFA nfa) {
        DFABuilder builder = new DFABuilder(nfa);
        return builder.dfa();
    }

    /**
     * Construct a DFABuilder object from a given NFA.  This simply constructs
     * an internal representation of the NFA that is easier to use for
     * conversion to a DFA.
     *
     * @param nfa the NFA to be converted
     */
    private DFABuilder(NFA nfa) {
        this.nfa = nfa;
        dfaStates = new HashMap<>();
        dfaAcceptingStates = new ArrayList<>();

        // DEBUG
        //System.out.print("NFA\n");
        //System.out.print("---\n");
        //dfaStateNames = new HashMap<>();
        //nfaStateNames = new HashMap<>();
        //Set<State> nfaStates = new HashSet<>();
        //State.collectStates(nfa.start, nfaStates);
        //for (State state : nfaStates) {
        //    nfaStateNames.put(state, nfaStateNames.size());
        //}
        //System.out.printf("Start = %d\n", nfaStateNames.get(nfa.start));
        //for (State state: nfaStates) {
        //    System.out.print(nfaStateNames.get(state));
        //    System.out.printf(": %s\n", state.token);
        //    for (Pair<Pair<Integer, Integer>, State> trans : state.neighbours) {
        //        System.out.printf("(%d, %s) -> %d\n",
        //                          nfaStateNames.get(state),
        //                          trans.left,
        //                          nfaStateNames.get(trans.right));
        //    }
        //}
        // END DEBUG
    }

    /**
     * Construct a DFA from the internal NFA representation.
     *
     * @return the constructed DFA
     */
    private DFA dfa() {
        // DEBUG
        //System.out.print("DFA\n");
        //System.out.print("---\n");
        // END DEBUG
        Set<State> nfaStartStates = new HashSet<>();
        nfaStartStates.add(nfa.start);
        epsClosure(nfaStartStates);
        State dfaStartState = discoverStates(nfaStartStates);
        // DEBUG
        //System.out.printf("Start = %d\n", dfaStateNames.get(dfaStartState));
        // END DEBUG
        return new DFA(dfaStartState, dfaAcceptingStates);
    }

    /**
     * Given a set of NFA nfaStates represented as a set of integer IDs,
     * construct the eps-closure of this set of nfaStates.
     *
     * @param nfaStates the set of nfaStates whose eps-closure is to be
     *                  computed
     */
    private void epsClosure(Set<State> nfaStates) {
        Queue<State> newStates = new LinkedList<>(nfaStates);
        while (!newStates.isEmpty()) {
            State state = newStates.remove();
            for (Pair<Pair<Integer, Integer>, State> trans : state.neighbours) {
                if (trans.left == null && !nfaStates.contains(trans.right)) {
                    newStates.add(trans.right);
                    nfaStates.add(trans.right);
                }
            }
        }
    }

    /**
     * Discover the set of DFA states reachable from the DFA start state.
     *
     * @param nfaStates map from sets of NFA nfaStates to corresponding DFA
     *                  state
     * @return the DFA state corresponding to this set of NFA states
     */
    private State discoverStates(Set<State> nfaStates) {
        State dfaState = dfaStates.get(nfaStates);
        if (dfaState == null) {
            Token.Name token = null;
            for (State nfaState : nfaStates) {
                if (token == null) {
                    token = nfaState.token;
                }
                else if (nfaState.token != null &&
                         nfaState.token.compareTo(token) < 0) {
                    token = nfaState.token;
                }
            }
            dfaState = new State(token);
            dfaStates.put(nfaStates, dfaState);
            // DEBUG
            //dfaStateNames.put(dfaState, dfaStateNames.size());
            //System.out.print(dfaStateNames.get(dfaState));
            //System.out.print(": ");
            //for (State nfaState : nfaStates) {
            //    System.out.print(nfaStateNames.get(nfaState));
            //    System.out.print(" ");
            //}
            //System.out.printf("%s\n", dfaState.token);
            // END DEBUG
            if (token != null) {
                dfaAcceptingStates.add(dfaState);
            }
            List<Pair<Integer, Integer>> charRanges =
                    discoverCharRanges(nfaStates);
            for (Pair<Integer, Integer> charRange : charRanges) {
                Set<State> nfaNeighbours = discoverNeighbours(nfaStates,
                                                              charRange);
                epsClosure(nfaNeighbours);
                State dfaNeighbour = discoverStates(nfaNeighbours);
                dfaState.addTransition(charRange, dfaNeighbour);
                // DEBUG
                //System.out.printf("(%d, %s) -> %d\n",
                //                  dfaStateNames.get(dfaState),
                //                  charRange,
                //                  dfaStateNames.get(dfaNeighbour));
                // END DEBUG
            }
        }
        return dfaState;
    }

    /**
     * For a set of NFA states, identify a minimum list of character ranges so
     * that any character in such a range leads to the same set of states from
     * these start states.
     *
     * @param nfaStates the set of start states
     * @return the list of character ranges
     */
    private List<Pair<Integer, Integer>> discoverCharRanges(
            Set<State> nfaStates) {
        List<Pair<Integer, Integer>> charRanges =
                nfaStates.stream()
                         .flatMap(state -> state.neighbours.stream())
                         .map(trans -> trans.left)
                         .filter(Objects::nonNull)
                         .collect(Collectors.toList());
        return CharRanges.subdivide(charRanges);
    }

    /**
     * Given a set of states and a character range, discover all states
     * reachable from this set of states for the characters in this character
     * range.
     *
     * @param nfaStates the set of start states
     * @param charRange the range of characters
     * @return the set of states reachable from <code>set</code> when reading
     * any character in <code>charRange</code>
     */
    private Set<State> discoverNeighbours(Set<State> nfaStates,
                                          Pair<Integer, Integer> charRange) {
        Set<State> nfaNeighbours = new HashSet<>();
        for (State state : nfaStates) {
            for (Pair<Pair<Integer, Integer>, State> trans : state.neighbours) {
                if (trans.left != null &&
                    charRange.left >= trans.left.left &&
                    charRange.right <= trans.left.right) {
                    nfaNeighbours.add(trans.right);
                }
            }
        }
        return nfaNeighbours;
    }

    private NFA                    nfa;
    private Map<Set<State>, State> dfaStates;
    private List<State>            dfaAcceptingStates;

    // DEBUG
    //private Map<State, Integer> nfaStateNames;
    //private Map<State, Integer> dfaStateNames;
    // END DEBUG
}
