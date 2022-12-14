package org.viapivov.exposer.container;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class WeakReferenceEqualsAware<T> extends WeakReference<T> {

    public WeakReferenceEqualsAware(T obj) {
        super(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WeakReference)) {
            return false;
        }
        WeakReference<?> other = (WeakReference<?>) obj;
        return Objects.equals(other.get(), this.get());
    }
}