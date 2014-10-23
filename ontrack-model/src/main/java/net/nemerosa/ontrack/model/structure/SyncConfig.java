package net.nemerosa.ontrack.model.structure;

import java.util.Collection;

public interface SyncConfig<T, D> {

    String getItemType();

    Collection<T> getSourceItems();

    Collection<T> getTargetItems();

    D getItemId(T item);

    void createTargetItem(T source);

    void replaceTargetItem(T source, T target);

    void deleteTargetItem(T target);

}
