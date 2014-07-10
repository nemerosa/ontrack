package net.nemerosa.ontrack.common;

import java.util.function.Supplier;

public class CachedSupplier<T> implements Supplier<T> {

    public static <T> CachedSupplier<T> of(Supplier<T> inner) {
        return new CachedSupplier<>(inner);
    }

    private final Supplier<T> inner;
    private T result;

    protected CachedSupplier(Supplier<T> inner) {
        this.inner = inner;
    }

    @Override
    public T get() {
        if (result == null) {
            result = inner.get();
        }
        return result;
    }
}
