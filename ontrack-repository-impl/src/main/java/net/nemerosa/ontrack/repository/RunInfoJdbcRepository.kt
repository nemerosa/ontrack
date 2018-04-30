package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.RunnableEntityType
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import net.nemerosa.ontrack.repository.support.getNullableInt
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
                            rs.getNullableInt("RUN_TIME"),
                            readSignature(rs)
                    )
                }
        ) ?: RunInfo.empty()
    }

    override fun deleteRunInfo(runnableEntityType: RunnableEntityType, id: Int): Ack {
        val runInfo = getRunInfo(runnableEntityType, id).takeIf { !it.empty }
        namedParameterJdbcTemplate.update(
                "DELETE FROM RUN_INFO WHERE ${runnableEntityType.name.toUpperCase()} = :entityId",
                params("entityId", id)
        )
        return Ack.validate(runInfo != null)
    }

    override fun setRunInfo(
            runnableEntityType: RunnableEntityType,
            id: Int,
            input: RunInfoInput,
            signature: Signature
    ): RunInfo {
        // Gets the existing run info if any
        val runInfo = getRunInfo(runnableEntityType, id).takeIf { !it.empty }
        // Parameters
        val params = params("runTime", input.runTime)
                .addValue("sourceType", input.sourceType)
                .addValue("sourceUri", input.sourceUri)
                .addValue("triggerType", input.triggerType)
                .addValue("triggerData", input.triggerData)
                .addValue("creation", Time.forStorage(signature.time))
                .addValue("creator", signature.user.name)
        // If existing, updates it
        if (runInfo != null) {
            namedParameterJdbcTemplate.update(
                    "UPDATE RUN_INFO " +
                            "SET SOURCE_TYPE = :sourceType, " +
                            "SOURCE_URI = :sourceUri, " +
                            "TRIGGER_TYPE = :triggerType, " +
                            "TRIGGER_DATA = :triggerData, " +
                            "CREATION = :creation, " +
                            "CREATOR = :creator, " +
                            "RUN_TIME = :runTime " +
                            "WHERE ID = :id",
                    params.addValue("id", runInfo.id)
            )
        }
        // Else, creates it
        else {
            namedParameterJdbcTemplate.update(
                    "INSERT INTO RUN_INFO(${runnableEntityType.name.toUpperCase()}, SOURCE_TYPE, SOURCE_URI, TRIGGER_TYPE, TRIGGER_DATA, RUN_TIME, CREATION, CREATOR) " +
                            "VALUES (:entityId, :sourceType, :sourceUri, :triggerType, :triggerData, :runTime, :creation, :creator)",
                    params.addValue("entityId", id)
            )
        }
        // OK
        return getRunInfo(runnableEntityType, id)
    }
}
