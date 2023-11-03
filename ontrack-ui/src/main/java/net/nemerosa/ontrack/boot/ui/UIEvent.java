package net.nemerosa.ontrack.boot.ui;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.model.support.NameValue;

import java.util.Map;

@Data
public class UIEvent {

    private final String eventType;
    private final String template;
    private final Signature signature;
    private final Map<ProjectEntityType, ProjectEntity> entities;
    private final Map<ProjectEntityType, ProjectEntity> extraEntities;
    private final ProjectEntityType ref;
    private final Map<String, NameValue> values;

    /**
     * Additional data processed from the values or entities
     */
    private final Map<String, ?> data;

}
