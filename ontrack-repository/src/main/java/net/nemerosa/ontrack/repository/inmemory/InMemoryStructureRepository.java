package net.nemerosa.ontrack.repository.inmemory;

import net.nemerosa.ontrack.model.exceptions.ProjectNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.repository.support.AbstractStructureRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStructureRepository extends AbstractStructureRepository {

    // IDs
    private final AtomicLong projectId = new AtomicLong();

    // Repositories
    private final Map<ID, Project> projects = new ConcurrentHashMap<>();

    @Override
    protected Project _createProject(Project project) {
        // Checks the name
        if (projects.values().stream().anyMatch(p -> StringUtils.equals(p.getName(), project.getName()))) {
            throw new ProjectNameAlreadyDefinedException(project.getName());
        }
        // Creates an ID
        ID id = id(projectId);
        // Updates the project with the ID
        Project created = project.withId(id);
        // Registers the project
        projects.put(id, created);
        // OK
        return created;
    }

    private ID id(AtomicLong id) {
        return ID.of(String.valueOf(id.incrementAndGet()));
    }
}
