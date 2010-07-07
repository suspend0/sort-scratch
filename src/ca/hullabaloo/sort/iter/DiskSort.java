package ca.hullabaloo.sort.iter;

import com.google.common.collect.*;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class DiskSort<T extends Comparable & Serializable> implements IteratorSort<T> {
    private final Ordering<T> ord = Ordering.natural().nullsFirst();
    private final int chunkSize;

    public DiskSort(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Iterator<T> sort(Iterator<T> iter) {
        List<File> files = Lists.newArrayList();
        try {
            writeSortedChunks(iter, files);
            List<Iterator<T>> iters = openStreams(files);
            return new SortingIterator<T>(ord, iters);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            for (File f : files)
                //noinspection ResultOfMethodCallIgnored
                f.delete();
        }
    }

    private List<Iterator<T>> openStreams(List<File> files) throws IOException {
        // NOTE that we use readUnshared() here b/c we don't want to keep a reference of what we read
        List<Iterator<T>> r = Lists.newArrayListWithCapacity(files.size());
        for (File f : files) {
            final ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            Iterator<T> iter = new AbstractIterator<T>() {
                int count = in.readInt();

                @Override
                protected T computeNext() {
                    if ((count--) == 0) return endOfData();
                    try {
                        //noinspection unchecked
                        return (T) in.readUnshared();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            r.add(iter);
        }
        return r;
    }

    private void writeSortedChunks(Iterator<T> iter, List<File> files) throws IOException {
        // NOTE that we use writeUnshared() here b/c we don't want to keep a reference of what we read
        Iterator<List<T>> chunks = Iterators.partition(iter, chunkSize);
        while (chunks.hasNext()) {
            File file = File.createTempFile("DiskSort-", ".ser");
            file.deleteOnExit();
            files.add(file);
            ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

            // Iterators.partition returns unmodifiable lists (why?)
            List<T> chunk = ord.sortedCopy(chunks.next());
            out.writeInt(chunk.size());
            for (T o : chunk)
                out.writeUnshared(o);

            out.close();
        }
    }

}
