package stbi.common.util;

/**
 * Pair of comparable values.
 *
 * Implementation of C++ Standard Template Library's pair.
 */
public class Pair<T1 extends Comparable<T1>, T2 extends Comparable<T2>> implements Comparable<Pair<T1, T2>> {
    public T1 first;
    public T2 second;

    public Pair(T1 _first, T2 _second) {
        first = _first;
        second = _second;
    }

    /**
     * Compare to other Pair of the same type
     *
     * @param other the other pair.
     * @return -1 if this < other, 0 if this == other, 1 if this > other
     */
    @Override
    public int compareTo(Pair<T1, T2> other) {
        int firstComp = first.compareTo(other.first);
        if (firstComp == 0) {
            return second.compareTo(other.second);
        }
        return firstComp;
    }
}
