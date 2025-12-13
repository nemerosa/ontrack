package net.nemerosa.ontrack.model.structure;

import java.util.Collection;

/**
 * @deprecated Will be removed in V6. Use syncForward instead
 */
@Deprecated
public interface SyncConfig<T, D> {

    String getItemType();

    Collection<T> getSourceItems();

    Collection<T> getTargetItems();

    D getItemId(T item);

    void createTargetItem(T source);

    void replaceTargetItem(T source, T target);

    void deleteTargetItem(T target);

    default boolean isTargetItemPresent(T targetItem) {
        return targetItem != null;
    }
}
