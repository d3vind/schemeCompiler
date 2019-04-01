package ca.dal.cs.schememm.common;

import java.io.*;

/**
 * CharStream class.  Produces a stream of characters (represented as their code
 * points).
 */
public class CharStream implements Stream<Integer> {

    /**
     * Construct a CharStream for the sequence of characters in a file.
     *
     * @param fileName the name of the file to read
     * @return a CharStream over the underlying file
     */
    public static CharStream fromFile(String fileName) throws IOException {
        return new CharStream(
                new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(fileName))));
    }

    /**
     * Construct a CharStream for the sequence of characters in a string.
     *
     * @param string the string to read
     * @return a CharStream over the underlying string
     */
    public static CharStream fromString(String string) throws IOException {
        return new CharStream(new StringReader(string));
    }

    /**
     * Construct a new CharStream from an underlying Reader object.
     *
     * @param reader the reader that provides the underlying character sequence
     */
    private CharStream(Reader reader) throws IOException {
        line = 1;
        col = 0;
        input = reader;
        item = -1;
        step();
    }

    @Override
    public Integer item() {
        return item >= 0 ? item : null;
    }

    @Override
    public Pair<Integer, Integer> pos() {
        return new Pair<>(line, col);
    }

    @Override
    public void step() throws IOException {
        if (item >= 0 && item == '\n') {
            line++;
            col = 1;
        }
        else {
            col++;
        }
        item = input.read();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        input.close();
    }

    private int item;
    private int line, col;
    private Reader input;
}
