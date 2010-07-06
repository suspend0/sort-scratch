package ca.hullabaloo.sort.iter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MemorySort<T extends Comparable> implements IteratorSort<T> {
    public Iterator<T> sort(Iterator<T> iter) {
        List<T> t = new ArrayList<T>();
        while(iter.hasNext())
            t.add(iter.next());
        Collections.sort(t);
        return t.iterator();
    }
}
