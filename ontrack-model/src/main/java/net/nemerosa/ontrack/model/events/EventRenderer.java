package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.support.NameValue;

public interface EventRenderer {

    String render(ProjectEntity projectEntity, Event event);

    String render(String valueKey, NameValue value, Event event);
}
