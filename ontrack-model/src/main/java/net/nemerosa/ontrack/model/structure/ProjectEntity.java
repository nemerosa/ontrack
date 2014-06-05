package net.nemerosa.ontrack.model.structure;

/**
 * A <b>ProjectEntity</b> is an {@link Entity} that belongs into a {@link Project}.
 */
public interface ProjectEntity extends Entity {

    /**
     * Returns the ID of the project that contains this entity. This method won't return <code>null</code>
     * but the ID could be {@linkplain ID#NONE undefined}.
     */
    ID getProjectId();

}
