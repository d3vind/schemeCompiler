package ca.dal.cs.schememm.scanner.fa;

import ca.dal.cs.schememm.common.CharRanges;
import ca.dal.cs.schememm.common.Pair;
import ca.dal.cs.schememm.scanner.Token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that implements the minimization of a DFA.
 */
class DFAMinimizer {

    /**
     * Minimize the given DFA
     *
     * @param dfa the DFA to be minimized
     * @return an equivalent DFA with the minimum number of states and
     * transitions.
     */
    static DFA run(DFA dfa) {
        DFAMinimizer minim = new DFAMinimizer(dfa);
        return minim.dfa();
    }

    /**
     * Construct a minimizer for the given DFA
     *
     * @param dfa the DFA to be minimized
     */
    private DFAMinimizer(DFA dfa) {
        Set<State> states = new HashSet<>();
        State.collectStates(dfa.start, states);
        Map<Token.Name, List<State>> classes = new HashMap<>();
        for (State state : states) {
            classes.putIfAbsent(state.token, new ArrayList<>());
            classes.get(state.token).add(state);
        }
        this.dfa = dfa;
        this.classes = new ArrayList<>(classes.values());
        classIds = new HashMap<>();
        representatives = new ArrayList<>();
    }

    /**
     * Construct and retrieve the minimized DFA.
     *
     * @return the minimized DFA
     */
    private DFA dfa() {
        //noinspection StatementWithEmptyBody
        while (splitClasses()) {
            // Do nothing
        }
        modifyTransitions();
        minimizeTransitions();
        return dfa;
    }

    /**
     * Split state classes into smaller subclasses based on having
     * out-neighbours in different classes.
     *
     * @return <code>true</code> if and only if at least one class was split
     * into subclasses.
     */
    private boolean splitClasses() {
        // Store a unique class ID for the states in each class
        for (int i = 0; i < classes.size(); ++i) {
            for (State state : classes.get(i)) {
                classIds.put(state, i);
            }
        }
        // Construct a new set of classes by splitting each class
        List<List<State>> newClasses =
                classes.stream()
                       .flatMap(cls -> splitClass(cls).stream())
                       .collect(Collectors.toList());
        // If there are more classes now, then at least one class was split
        if (classes.size() < newClasses.size()) {
            classes = newClasses;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Split a class into subclasses based on the membership of out-neighbours
     * for different characters in different character classes.
     *
     * @param cls the class to be split
     * @return a stream of the produced subclasses
     */
    private List<List<State>> splitClass(List<State> cls) {
        List<Pair<Integer, Integer>> repClasses =
                CharRanges.subdivide(
                        cls.stream()
                           .flatMap(state -> state.neighbours.stream())
                           .map(nbr -> nbr.left)
                           .collect(Collectors.toList()));
        List<Integer> repChars = repClasses.stream()
                                           .map(range -> range.left)
                                           .collect(Collectors.toList());
        Map<List<Integer>, List<State>> subClasses = new HashMap<>();
        for (State state : cls) {
            List<Integer> neighbours =
                    repChars.stream()
                            .map(ch -> classIds.get(state.transition(ch)))
                            .collect(Collectors.toList());
            subClasses.putIfAbsent(neighbours, new ArrayList<>());
            subClasses.get(neighbours).add(state);
        }
        return new ArrayList<>(subClasses.values());
    }

    /**
     * Modify the transitions of the states of the current DFA so each target
     * state is replaced with the representative of the class it is contained
     * in.
     */
    private void modifyTransitions() {
        int i = 0;
        for (List<State> cls : classes) {
            representatives.add(cls.get(0));
            for (State state : cls) {
                classIds.put(state, i);
            }
            ++i;
        }
        representatives.set(classIds.get(dfa.start), dfa.start);
        for (State state : representatives) {
            state.neighbours =
                    state.neighbours
                            .stream()
                            .map(trans -> new Pair<>(
                                    trans.left,
                                    representatives
                                            .get(classIds.get(trans.right))))
                            .collect(Collectors.toList());
        }
        List<State> accepting = new ArrayList<>();
        for (State state : dfa.accepting) {
            if (state == representatives.get(classIds.get(state))) {
                accepting.add(state);
            }
        }
        dfa.accepting = accepting;
    }

    /**
     * Merge character ranges that lead to the same neighbour state
     */
    private void minimizeTransitions() {
        for (State state : representatives) {
            Map<State, List<Pair<Integer, Integer>>> transitions =
                    new HashMap<>();
            for (Pair<Pair<Integer, Integer>, State> trans : state.neighbours) {
                transitions.putIfAbsent(trans.right, new ArrayList<>());
                transitions.get(trans.right).add(trans.left);
            }
            state.neighbours.clear();
            for (State neighbour : transitions.keySet()) {
                state.neighbours
                        .addAll(CharRanges.simplify(transitions.get(neighbour))
                                          .stream()
                                          .map(rng -> new Pair<>(rng,
                                                                 neighbour))
                                          .collect(Collectors.toList()));
            }
        }
    }

    private DFA                 dfa;
    private List<List<State>>   classes;
    private Map<State, Integer> classIds;
    private List<State>         representatives;
}
