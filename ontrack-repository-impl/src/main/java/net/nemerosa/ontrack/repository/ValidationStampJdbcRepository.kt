package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class ValidationStampJdbcRepository(
    dataSource: DataSource,
    private val branchJdbcRepositoryAccessor: BranchJdbcRepositoryAccessor,
    private val validationDataTypeConfigRepository: ValidationDataTypeConfigRepository,
) : AbstractJdbcRepository(dataSource), ValidationStampRepository, ValidationStampJdbcRepositoryAccessor {

    override fun findByToken(token: String): List<ValidationStamp> =
        namedParameterJdbcTemplate!!.query(
            """
                    SELECT *
                    FROM validation_stamps
                    WHERE name ILIKE :name
                """,
            mapOf("name" to "%$token%")
        ) { rs, _ ->
            toValidationStamp(rs)
        }

    override fun toValidationStamp(rs: ResultSet) = ValidationStamp(
        id = id(rs),
        name = rs.getString("name"),
        description = rs.getString("description"),
        branch = branchJdbcRepositoryAccessor.getBranch(id(rs, "branchid")),
        isImage = !rs.getString("imagetype").isNullOrBlank(),
        signature = readSignature(rs),
        dataType = validationDataTypeConfigRepository.readValidationDataTypeConfig<Any>(rs),
        owner = null,
    )
}