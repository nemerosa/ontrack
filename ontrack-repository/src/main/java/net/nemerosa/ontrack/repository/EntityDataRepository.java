package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.Optional;

/**
 * Store for data associated with project entities.
 */
public interface EntityDataRepository {

    /**
     * Store
     */
    void store(ProjectEntity entity, String key, String value);

    /**
     * Retrieve
     */
    Optional<String> retrieve(ProjectEntity entity, String key);

    /**
     * Delete
     */
    void delete(ProjectEntity entity, String key);

}
