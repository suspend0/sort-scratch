package ca.hullabaloo.sort.iter;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Merges sorted iterators to produce a sorted result
 */
class MergeSortedIterator<T> extends AbstractIterator<T> {
    private final PriorityQueue<PeekingIterator<T>> heap;

    /**
     * @param sortedData all of the Iterators provided in must be sorted
     */
    public static <T> Iterator<T> create(Comparator<T> ord, List<Iterator<T>> sortedData) {
        if (sortedData.isEmpty())
            return Iterators.emptyIterator();
        return new MergeSortedIterator<T>(ord, sortedData);
    }

    private MergeSortedIterator(final Comparator<T> ord, List<Iterator<T>> iterators) {
        Comparator<PeekingIterator<T>> compareHead = new Comparator<PeekingIterator<T>>() {
            public int compare(PeekingIterator<T> o1, PeekingIterator<T> o2) {
                return ord.compare(o1.peek(), o2.peek());
            }
        };

        // binary heap keeps the iterators sorted.
        this.heap = new PriorityQueue<PeekingIterator<T>>(iterators.size(), compareHead);

        // Our comparator barfs on empty iterators, so we're careful not to add them to the heap
        for (Iterator<T> it : iterators) {
            PeekingIterator<T> peek = Iterators.peekingIterator(it);
            if (peek.hasNext())
                heap.add(peek);
        }

    }

    @Override
    protected T computeNext() {
        // the top of the heap is the next item to return,
        // we pop the value off the Iterator with next(), and
        // then we add it back, trusting the heap to put it in
        // the right order.
        PeekingIterator<T> head = heap.poll();
        if (head == null) return endOfData();
        T result = head.next();
        // this is how we empty the heap
        if (head.hasNext())
            heap.add(head);
        return result;
    }

}
