package net.nemerosa.ontrack.repository.support.store;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Signature;

@Data
public class EntityDataStoreRecord implements Comparable<EntityDataStoreRecord> {

    private final int id;
    private final ProjectEntity entity;
    private final String category;
    private final String name;
    private final String groupName;
    private final Signature signature;
    private final JsonNode data;

    @Override
    public int compareTo(EntityDataStoreRecord o) {
        int i = -this.signature.getTime().compareTo(o.signature.getTime());
        if (i != 0) {
            return i;
        } else {
            return o.id - this.id;
        }
    }
}
