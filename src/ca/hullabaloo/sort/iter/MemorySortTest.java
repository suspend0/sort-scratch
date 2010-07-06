package ca.hullabaloo.sort.iter;

public class MemorySortTest extends IteratorSortTestCase {
    @Override
    protected IteratorSort<String> sorter(int size) {
        return new MemorySort<String>();
    }

    public void testLargeSort() {
        // heap to small for in-memory sorting
    }
}
