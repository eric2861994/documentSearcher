package stbi.common.util;

/**
 * Pair of values.
 */
public class Pair<T1 extends Comparable<T1>, T2> implements Comparable<Pair<T1, T2>> {
    public T1 first;
    public T2 second;

    public Pair(T1 _first, T2 _second) {
        first = _first;
        second = _second;
    }

    @Override
    public int compareTo(Pair<T1, T2> that) {
        return this.first.compareTo(that.first);
    }
}
