package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertySearchArguments;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Repository to access the properties.
 */
public interface PropertyRepository {

    TProperty loadProperty(String typeName, ProjectEntityType entityType, ID entityId);

    void saveProperty(String typeName, ProjectEntityType entityType, ID entityId, JsonNode data);

    Ack deleteProperty(String typeName, ProjectEntityType entityType, ID entityId);

    Collection<ProjectEntity> searchByProperty(String typeName,
                                               BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
                                               Predicate<TProperty> predicate);

    @Nullable
    ID findBuildByBranchAndSearchkey(ID branchId, String typeName, PropertySearchArguments searchArguments);
}
