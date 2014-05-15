package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.model.structure.Container;

import java.util.Collection;
import java.util.Optional;

public final class JsonViewClass {

    private JsonViewClass() {
    }

    public static Class<?> getViewClass(Object any) {
        if (any instanceof Container<?>) {
            Optional<?> first = ((Container<?>) any).first();
            if (first.isPresent()) {
                return getViewClass(first.get());
            } else {
                return Object.class;
            }
        } else if (any instanceof Collection<?>) {
            return Object.class;
        } else if (any != null) {
            return any.getClass();
        } else {
            return Object.class;
        }
    }

}
