package net.nemerosa.ontrack.model.support;

import lombok.Data;

import java.util.Collection;

/**
 * Simple implementation of a {@link Selectable} item based on a name only.
 */
@Data
public class SelectableString implements Selectable {

    private final boolean selected;
    private final String name;

    @Override
    public String getId() {
        return name;
    }

    public static SelectableString of(String name, Collection<String> names) {
        return new SelectableString(
                names.contains(name),
                name
        );
    }
}
