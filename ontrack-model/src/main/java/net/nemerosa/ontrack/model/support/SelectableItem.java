package net.nemerosa.ontrack.model.support;

import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Simple implementation of a {@link Selectable} item.
 */
@Data
public class SelectableItem implements Selectable {

    private final boolean selected;
    private final String id;
    private final String name;

    /**
     * Creation of a list of selectable items from a list of items, using an extractor for the id and the name,
     * and a predicate for the selection.
     */
    public static <T> List<SelectableItem> listOf(
            Collection<T> items,
            Function<T, String> idFn,
            Function<T, String> nameFn,
            Predicate<T> selectedFn
    ) {
        return items.stream()
                .map(i ->
                        new SelectableItem(
                                selectedFn.test(i),
                                idFn.apply(i),
                                nameFn.apply(i)
                        )
                )
                .collect(Collectors.toList());
    }


}
