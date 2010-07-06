package ca.hullabaloo.sort.iter;

import com.google.common.collect.*;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class DiskSort<T extends Comparable & Serializable> implements IteratorSort<T> {
    private final Ordering<T> ord = Ordering.natural().nullsLast();
    private final int chunkSize;

    public DiskSort(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Iterator<T> sort(Iterator<T> iter) {
        List<File> files = Lists.newArrayList();
        try {
            writeSortedChunks(iter, files);
            List<PeekingIterator<T>> iters = openStreams(files);
            return new SortingIterator(iters);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            for (File f : files)
                //noinspection ResultOfMethodCallIgnored
                f.delete();
        }
    }

    private List<PeekingIterator<T>> openStreams(List<File> files) throws IOException {
        List<PeekingIterator<T>> r = Lists.newArrayListWithCapacity(files.size());
        for (File f : files) {
            final ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            Iterator<T> iter = new AbstractIterator<T>() {
                int count = in.readInt();

                @Override
                protected T computeNext() {
                    if ((--count) == 0) return endOfData();
                    try {
                        //noinspection unchecked
                        return (T) in.readObject();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            r.add(Iterators.peekingIterator(iter));
        }
        return r;
    }

    private void writeSortedChunks(Iterator<T> iter, List<File> files) throws IOException {
        Iterator<List<T>> chunks = Iterators.partition(iter, chunkSize);
        while (chunks.hasNext()) {
            File file = File.createTempFile("DiskSort-", ".ser");
            file.deleteOnExit();
            files.add(file);
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

            List<T> chunk = ord.sortedCopy(chunks.next());
            out.writeInt(chunk.size());
            for (T o : chunk)
                out.writeObject(o);

            out.close();
        }
    }

    private class SortingIterator extends AbstractIterator<T> {
        private final List<PeekingIterator<T>> iters;

        public SortingIterator(List<PeekingIterator<T>> iters) {
            this.iters = iters;
        }

        @Override
        protected T computeNext() {
            PeekingIterator<T> empty = Iterators.peekingIterator(Iterators.<T>singletonIterator(null));
            PeekingIterator<T> minSoFar = empty;
            for (PeekingIterator<T> iter : iters) {
                if (iter.hasNext() && ord.compare(minSoFar.peek(), iter.peek()) < 0) {
                    minSoFar = iter;
                }
            }
            if (minSoFar == empty)
                return endOfData();
            return minSoFar.next();
        }
    }
}
