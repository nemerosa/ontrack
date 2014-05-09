package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.exceptions.ProjectNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

@Repository
public class StructureJdbcRepository extends AbstractJdbcRepository implements StructureRepository {

    @Autowired
    public StructureJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Project newProject(Project project) {
        // Validation
        notNull(project, "Project must be defined");
        isTrue(project.getId() == null || !project.getId().isSet(), "Project ID must not be defined");
        // Creation
        try {
            int id = dbCreate(
                    "INSERT INTO PROJECTS(NAME, DESCRIPTION) VALUES (:name, :description)",
                    params("name", project.getName()).addValue("description", project.getDescription())
            );
            // Returns with ID
            return project.withId(id(id));
        } catch (DuplicateKeyException ex) {
            throw new ProjectNameAlreadyDefinedException(project.getName());
        }
    }

}
