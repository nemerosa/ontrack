package net.nemerosa.ontrack.model.structure;

public interface Decorator {

    /**
     * Gets a decoration for this entity.
     *
     * @param entity Entity
     * @return A decoration to apply or <code>null</code> if none.
     */
    Decoration getDecoration(ProjectEntity entity);

}
