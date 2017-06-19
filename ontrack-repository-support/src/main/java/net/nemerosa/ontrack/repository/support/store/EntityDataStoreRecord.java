package net.nemerosa.ontrack.repository.support.store;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Signature;

@Data
public class EntityDataStoreRecord {

    private final int id;
    private final ProjectEntity entity;
    private final String category;
    private final String name;
    private final String groupName;
    private final Signature signature;
    private final JsonNode data;

}
