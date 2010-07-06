package ca.hullabaloo.sort.iter;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Ordering;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"UnusedDeclaration"})
public abstract class IteratorSortTestCase extends TestCase {
    protected abstract IteratorSort<String> sorter(int size);

    public void testEmpty() {
        testSort(0);
    }

    public void testSortWithNull() {
        List<String> source = Arrays.asList("foo", null, "bar");
        testSort(source.size(), source.iterator());
    }

    public void testTinySort() {
        testSort(4 * pow(100, 0));
    }

    public void testLittleSort() {
        testSort(4 * pow(100, 1));
    }

    public void testMediumSort() {
        testSort(4 * pow(100, 2));
    }

    public void testLargeSort() {
        testSort(2 * pow(100, 3));
    }

    int pow(int base, int exponent) {
        int r = 1;
        while ((--exponent) >= 0)
            r *= base;
        return r;
    }

    private void testSort(int strings) {
        testSort(strings, new RandomStrings(strings));
    }

    private void testSort(int strings, Iterator<String> iter) {
        IteratorSort<String> sorter = sorter(strings);
        Ordering<String> cmp = Ordering.natural().nullsFirst();
        final Iterator<String> sorted = sorter.sort(iter);
        assertIsOrdered(cmp, strings, sorted);
    }

    private <T> void assertIsOrdered(Ordering<T> cmp, int expectedSize, Iterator<T> sorted) {
        int count = 0;
        if (sorted.hasNext()) {
            count = 1;
            T prev = sorted.next();
            while (sorted.hasNext()) {
                count++;
                T next = sorted.next();
                if (cmp.compare(prev, next) > 0) {
                    fail();
                }
                prev = next;
            }
        }
        assertEquals(expectedSize, count);
    }


    protected static class RandomStrings extends AbstractIterator<String> {
        private final Random rand = new Random();
        private int count;

        public RandomStrings(int count) {
            this.count = count;
        }

        @Override
        protected String computeNext() {
            if (count-- <= 0) return endOfData();
            return randomString();
        }

        private String randomString() {
            int len = 150;
            char[] chars = new char[len];
            for (int i = 0; i < len; i++)
                chars[i] = (char) (32 + rand.nextInt(94));
            return new String(chars);
        }
    }
}
