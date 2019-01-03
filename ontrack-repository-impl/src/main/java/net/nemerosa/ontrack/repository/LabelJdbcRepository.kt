package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class LabelJdbcRepository(
        dataSource: DataSource
) : AbstractJdbcRepository(dataSource), LabelRepository {
    override val labels: List<LabelRecord>
        get() = jdbcTemplate.query(
                "SELECT * FROM LABEL ORDER BY CATEGORY, NAME"
        ) { rs, _ ->
            LabelRecord(
                    id = rs.getInt("ID"),
                    category = rs.getString("CATEGORY"),
                    name = rs.getString("NAME"),
                    description = rs.getString("DESCRIPTION"),
                    color = rs.getString("COLOR"),
                    computedBy = rs.getString("COMPUTED_BY")
            )
        }
}