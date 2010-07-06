package ca.hullabaloo.sort.iter;

import com.google.common.collect.*;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.List;

public class SortingIteratorTest extends TestCase {
    private final Ordering<String> ord = Ordering.natural().nullsFirst();

    public void testOne() {
        List<String> s = ImmutableList.of("a", "b", "c");
        runTest(s);
    }

    public void testTwo() {
        List<String> s1 = ImmutableList.of("a", "b", "z");
        List<String> s2 = ImmutableList.of("w", "q", "c");
        runTest(s1, s2);
    }

    public void testThree() {
        List<String> s1 = ImmutableList.of("a", "b", "z");
        List<String> s2 = ImmutableList.of("w", "q", "c");
        List<String> s3 = ImmutableList.of("l", "x", "p", "f");
        runTest(s1, s2, s3);
    }

    private void runTest(List<String>... s) {
        for (int i = 0; i < s.length; i++)
            s[i] = ord.sortedCopy(s[i]);
        List<Iterator<String>> iters = Lists.newArrayList();
        List<String> expected = Lists.newArrayList();
        for (List<String> x : s) {
            iters.add(x.iterator());
            expected.addAll(x);
        }
        expected = ord.sortedCopy(expected);
        Iterator<String> sorted = new SortingIterator<String>(ord, iters);
        assertEquals(expected, ImmutableList.copyOf(sorted));
    }
}
