package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import lombok.experimental.Builder;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Data
@Builder
public class SyncConfig<T, D> {

    private final String itemType;
    private final Supplier<Collection<T>> sourceItems;
    private final Supplier<Collection<T>> targetItems;
    private final Function<T, D> itemId;
    private final Function<D, Optional<T>> targetItem;
    private final Consumer<T> createTargetItem;
    private final BiConsumer<T, T> replaceTargetItem;
    private final Consumer<T> deleteTargetItem;

}
