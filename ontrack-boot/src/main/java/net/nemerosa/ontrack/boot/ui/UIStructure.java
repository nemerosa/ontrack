package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;

public interface UIStructure {

    Project newProject(NameDescription nameDescription);
}
