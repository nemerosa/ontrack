package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.exceptions.ProjectNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

@Repository
public class StructureJdbcRepository extends AbstractJdbcRepository implements StructureRepository {

    private final StructureFactory structureFactory;

    @Autowired
    public StructureJdbcRepository(DataSource dataSource, StructureFactory structureFactory) {
        super(dataSource);
        this.structureFactory = structureFactory;
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

    @Override
    public List<Project> getProjectList() {
        return getJdbcTemplate().query(
                "SELECT * FROM PROJECTS ORDER BY NAME",
                (rs, rowNum) -> toProject(rs)
        );
    }

    @Override
    public Project getProject(ID projectId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                "SELECT * FROM PROJECTS WHERE ID = :id",
                params("id", projectId.getValue()),
                (rs, rowNum) -> toProject(rs)
        );
    }

    private Project toProject(ResultSet rs) throws SQLException {
        return structureFactory.newProject(new NameDescription(
                rs.getString("name"),
                rs.getString("description")
        )).withId(id(rs.getInt("id")));
    }

}
