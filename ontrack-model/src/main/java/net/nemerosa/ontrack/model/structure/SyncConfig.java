package net.nemerosa.ontrack.model.structure;

import java.util.Collection;
import java.util.Optional;

public interface SyncConfig<T, D> {

    String getItemType();

    Collection<T> getSourceItems();

    Collection<T> getTargetItems();

    D getItemId(T item);

    Optional<T> getTargetItem(D id);

    void createTargetItem(T source);

    void replaceTargetItem(T source, T target);

    void deleteTargetItem(T target);

}
