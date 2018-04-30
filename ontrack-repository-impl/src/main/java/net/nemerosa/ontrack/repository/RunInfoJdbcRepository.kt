package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunnableEntityType
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class RunInfoJdbcRepository(
        dataSource: DataSource
) : AbstractJdbcRepository(dataSource), RunInfoRepository {
    override fun getRunInfo(runnableEntityType: RunnableEntityType, id: Int): RunInfo {
        return getFirstItem(
                "SELECT * FROM RUN_INFO WHERE ${runnableEntityType.name.toUpperCase()} = :entityId",
                params("entityId", id),
                { rs, _ ->
                    RunInfo(
                            rs.getInt("ID"),
                            rs.getString("SOURCE_TYPE"),
                            rs.getString("SOURCE_URI"),
                            rs.getString("TRIGGER_TYPE"),
                            rs.getString("TRIGGER_DATA"),
                            rs.getObject("RUN_TIME", Int::class.java)
                    )
                }
        ) ?: RunInfo.empty()
    }
}
