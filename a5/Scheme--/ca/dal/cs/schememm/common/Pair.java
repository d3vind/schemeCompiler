package ca.dal.cs.schememm.common;

/**
 * Represents a pair of things
 *
 * @param <L> type of the first member
 * @param <R> type of the second member
 */
public class Pair<L, R> {

    /**
     * Construct a pair from its two components
     *
     * @param left  the first component
     * @param right the second component
     */
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair other = (Pair) obj;
        return this.left.equals(other.left) &&
               this.right.equals(other.right);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", left, right);
    }

    /**
     * The first member of the pair
     */
    public final L left;

    /**
     * The second member of the pair
     */
    public final R right;
}
