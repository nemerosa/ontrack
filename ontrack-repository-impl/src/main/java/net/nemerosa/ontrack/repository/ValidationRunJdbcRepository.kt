package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunData
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class ValidationRunJdbcRepository(
    private val dataSource: DataSource,
) : AbstractJdbcRepository(dataSource), ValidationRunRepository {

    override fun updateValidationRunData(
        run: ValidationRun,
        data: ValidationRunData<*>?,
    ): ValidationRun {
        if (data != null) {
            namedParameterJdbcTemplate!!.update(
                "INSERT INTO VALIDATION_RUN_DATA(VALIDATION_RUN, DATA_TYPE_ID, DATA) VALUES (:validationRunId, :dataTypeId, CAST(:data AS JSONB))",
                mapOf(
                    "validationRunId" to run.id(),
                    "dataTypeId" to data.descriptor.id,
                    "data" to writeJson(data.data),
                )
            )
        } else {
            namedParameterJdbcTemplate!!.update(
                "DELETE FROM VALIDATION_RUN_DATA WHERE VALIDATION_RUN = :validationRunId",
                mapOf(
                    "validationRunId" to run.id(),
                )
            )
        }
        return run.withData(data)
    }

}