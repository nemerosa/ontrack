package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
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

    override fun findBranchesWithValidationStamp(project: Project, validation: String): List<Branch> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT B.ID
                FROM BRANCHES B
                INNER JOIN VALIDATION_STAMPS VS ON VS.BRANCHID = B.ID
                LEFT JOIN (
                	SELECT DISTINCT ON (BRANCHID) BRANCHID, CREATION
                	FROM BUILDS
                	ORDER BY BRANCHID, CREATION DESC
                ) LAST_BUILD ON LAST_BUILD.BRANCHID = B.ID
                WHERE B.PROJECTID = :projectId
                AND VS.NAME = :validation
                AND B.DISABLED = FALSE
                ORDER BY COALESCE(LAST_BUILD.CREATION, B.CREATION) DESC
            """.trimIndent(),
            mapOf(
                "projectId" to project.id(),
                "validation" to validation,
            )
        ) { rs, _ ->
            branchJdbcRepositoryAccessor.getBranch(id(rs), project)
        }

    override fun findValidationStampsForNames(branch: Branch, validationStamps: List<String>): List<ValidationStamp> =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT *
                FROM VALIDATION_STAMPS VS
                WHERE VS.BRANCHID = :branchId
                AND VS.NAME IN (:validations)
                ORDER BY VS.NAME
            """.trimIndent(),
            mapOf(
                "branchId" to branch.id(),
                "validations" to validationStamps,
            )
        ) { rs, _ ->
            toValidationStamp(rs, branch = branch)
        }

    override fun toValidationStamp(rs: ResultSet, branch: Branch?) = ValidationStamp(
        id = id(rs),
        name = rs.getString("name"),
        description = rs.getString("description"),
        branch = branch ?: branchJdbcRepositoryAccessor.getBranch(id(rs, "branchid")),
        isImage = !rs.getString("imagetype").isNullOrBlank(),
        signature = readSignature(rs),
        dataType = validationDataTypeConfigRepository.readValidationDataTypeConfig<Any>(rs),
        owner = null,
    )
}