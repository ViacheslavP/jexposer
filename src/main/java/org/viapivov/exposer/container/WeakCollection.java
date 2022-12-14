package org.viapivov.exposer.container;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeakCollection<T> implements Collection<T> {

    public final ConcurrentLinkedQueue<WeakReference<T>> queue;

    public WeakCollection() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public int size() {
        cleanup();
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        cleanup();
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        cleanup();
        WeakReference<?> ref = new WeakReferenceEqualsAware<>(o);
        return queue.stream().anyMatch(el -> Objects.equals(el, ref));
    }

    @Override
    public Iterator<T> iterator() {
        return stream().iterator();
    }

    @Override
    public Object[] toArray() {
        return stream().collect(Collectors.toList()).toArray();

    }

    @Override
    @SuppressWarnings("hiding")
    public <T> T[] toArray(T[] a) {
        return stream().collect(Collectors.toList()).toArray(a);
    }

    @Override
    public boolean add(T e) {
        WeakReference<T> weakReference = new WeakReferenceEqualsAware<T>(e);
        return queue.add(weakReference);
    }

    @Override
    public boolean remove(Object o) {
        Iterator<WeakReference<T>> iterator = queue.iterator();
        while (iterator.hasNext()) {
            WeakReference<T> reference = iterator.next();
            if (reference.get() != null && Objects.equals(reference.get(), o)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Stream<Boolean> stream = c.stream().map(this::contains);
        return terminateAll(stream);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        Stream<Boolean> stream = c.stream().map(this::add);
        return terminateAll(stream);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Stream<Boolean> stream = c.stream().map(this::remove);
        return terminateAll(stream);
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        Set<?> set;
        if (!(c instanceof Set)) {
            set = c.stream().collect(Collectors.toSet());
        } else {
            set = (Set<?>) c;
        }
        Stack<T> stack = new Stack<>();
        boolean result = stream().filter(set::contains).map(stack::add).reduce(false, (a, b) -> a | b);
        queue.clear();
        stack.forEach(this::add);
        return result;
    }

    @Override
    public void clear() {
        queue.clear();
    }

    private boolean terminateAll(Stream<Boolean> stream) {
        return stream.reduce(false, (a, b) -> a | b);
    }

    private void cleanup() {
        Iterator<WeakReference<T>> iterator = queue.iterator();
        while (iterator.hasNext()) {
            WeakReference<T> reference = iterator.next();
            if (reference.get() == null) {
                iterator.remove();
            }
        }
    }

    public Stream<T> stream() {
        return queue.stream().map(WeakReference::get).filter(Objects::nonNull);
    }

}
