package net.nemerosa.ontrack.extension.git.repository

import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class GitRepositoryJdbcHelper(dataSource: DataSource) : AbstractJdbcRepository(dataSource), GitRepositoryHelper {

    override fun findBranchWithProjectAndGitBranch(project: Project, gitBranch: String): Int? {
        return namedParameterJdbcTemplate.queryForObject(
                """SELECT b.ID FROM PROPERTIES p
                        INNER JOIN BRANCHES b ON b.ID = p.BRANCH
                        WHERE p.TYPE = :type
                        AND p.SEARCHKEY = :branch
                        AND b.PROJECTID = :project
                        """,
                params("type", GitBranchConfigurationPropertyType::class.java.name)
                        .addValue("branch", gitBranch)
                        .addValue("project", project.id()),
                Int::class.java
        )
    }


}