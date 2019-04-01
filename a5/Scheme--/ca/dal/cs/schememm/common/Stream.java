package ca.dal.cs.schememm.common;

import ca.dal.cs.schememm.scanner.LexicalError;

import java.io.IOException;

/**
 * A Stream is a sequence of objects that support inspecting the current object
 * in the sequence and advancing to the next object in the sequence.
 *
 * @param <T> the type of objects in the sequence
 */
public interface Stream<T> {

    /**
     * Returns the current item in the sequence, null if we are at the end of
     * the sequence.
     *
     * @return the current item in the sequence
     */
    T item();

    /**
     * Returns the position of the current item in the underlying character
     * stream.
     *
     * @return a pair (line, column)
     */
    Pair<Integer, Integer> pos();

    /**
     * Advance to the next item in the input stream
     */
    void step() throws IOException, LexicalError;
}
