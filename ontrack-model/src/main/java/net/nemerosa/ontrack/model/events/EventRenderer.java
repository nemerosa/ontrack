package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.ProjectEntity;

public interface EventRenderer {

    String render(ProjectEntity projectEntity, Event event);

}
