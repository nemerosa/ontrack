package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ScmIndexIssueRepository(dataSource: DataSource) : AbstractJdbcRepository(dataSource) {
}