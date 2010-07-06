package ca.hullabaloo.sort.iter;

public class DiskSortTest extends IteratorSortTestCase {
    @Override
    protected IteratorSort<String> sorter(int size) {
        size = Math.max(size,1);
        size /= 3;
        size = Math.max(size,100);
        size = Math.min(size,200000);
        return new DiskSort<String>(size);
    }
}