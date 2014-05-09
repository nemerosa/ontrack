package net.nemerosa.ontrack.repository.support;

import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureRepository;

public abstract class AbstractStructureRepository implements StructureRepository {

    @Override
    public Project newProject(Project project) {
        if (project.getId().isSet()) {
            throw new IllegalStateException("Cannot save a new project with its ID being already set.");
        } else {
            return _createProject(project);
        }
    }

    protected abstract Project _createProject(Project project);
}
