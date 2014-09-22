package net.nemerosa.ontrack.boot.ui;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.Signature;

import java.util.Map;

@Data
public class UIEvent {

    private final String template;
    private final Signature signature;
    private final Map<ProjectEntityType, ProjectEntity> entities;
    private final Map<String, String> values;

}
