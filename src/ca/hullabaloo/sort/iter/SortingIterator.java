package ca.hullabaloo.sort.iter;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

class SortingIterator<T> extends AbstractIterator<T> {
    private final PriorityQueue<Item<T>> items;

    public SortingIterator(Comparator<T> ord, List<Iterator<T>> iters) {
        this.items = new PriorityQueue<Item<T>>(Math.max(1,iters.size()));
        for (Iterator<T> it : iters)
            if (it.hasNext())
                this.items.add(new Item<T>(ord, it));
    }

    @Override
    protected T computeNext() {
        Item<T> item = items.poll();
        if (item == null) return endOfData();
        T result = item.iter.next();
        if (item.iter.hasNext())
            items.add(item);
        return result;
    }

    private class Item<T> implements Comparable<Item<T>> {
        private final Comparator<T> ord;
        private final PeekingIterator<T> iter;

        public Item(Comparator<T> ord, Iterator<T> iter) {
            this.ord = ord;
            this.iter = Iterators.peekingIterator(iter);
        }

        public int compareTo(Item<T> o) {
            return ord.compare(iter.peek(), o.iter.peek());
        }
    }
}
