package ca.hullabaloo.sort.iter;

import java.util.Iterator;

public interface IteratorSort<T extends Comparable> {
    public Iterator<T> sort(Iterator<T> iter);
}
