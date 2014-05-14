package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.model.structure.Container;

import java.util.Collection;
import java.util.Optional;

public final class JsonViewClass {

    private JsonViewClass() {
    }

    public static Class<?> getViewClass(Object any) {
        if (any instanceof Container<?>) {
            return getViewClassForOptional(((Container<?>) any).first());
        } else if (any instanceof Collection<?>) {
            return getViewClassForOptional(((Collection<?>) any).stream().findFirst());
        } else if (any != null) {
            return any.getClass();
        } else {
            return Object.class;
        }
    }

    private static Class<?> getViewClassForOptional(Optional<?> first) {
        if (first.isPresent()) {
            return getViewClass(first.get());
        } else {
            return Object.class;
        }
    }
}
