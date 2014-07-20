package net.nemerosa.ontrack.model.structure;

import lombok.Data;

/**
 * Abstract information added to an entity.
 */
@Data
public class EntityInformation {

    private final ProjectEntityType type;
    private final ID id;
    private final String extensionType;
    private final Object extensionData;

}
