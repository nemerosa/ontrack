package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class PromotionLevelJdbcRepository(
    dataSource: DataSource,
    private val branchJdbcRepositoryAccessor: BranchJdbcRepositoryAccessor,
) : AbstractJdbcRepository(dataSource), PromotionLevelRepository, PromotionLevelJdbcRepositoryAccessor {

    override fun getPromotionLevel(id: ID, branch: Branch?): PromotionLevel =
        getFirstItem(
            """
               SELECT *
                FROM promotion_levels
                WHERE id = :id
            """,
            params("id", id.value)
        ) { rs, _ ->
            toPromotionLevel(rs, branch)
        }

    override fun findNamesByToken(token: String?): List<String> =
        if (token.isNullOrBlank()) {
            jdbcTemplate!!.queryForList(
                """SELECT DISTINCT(NAME) FROM PROMOTION_LEVELS ORDER BY NAME""",
                String::class.java
            )
        } else {
            namedParameterJdbcTemplate!!.queryForList(
                """SELECT DISTINCT(NAME) FROM PROMOTION_LEVELS WHERE NAME ILIKE :name ORDER BY NAME""",
                mapOf("name" to "%$token%"),
                String::class.java
            )
        }

    override fun findByToken(token: String?): List<PromotionLevel> =
        if (token.isNullOrBlank()) {
            jdbcTemplate!!.query(
                """
                    SELECT *
                    FROM promotion_levels
                """
            ) { rs, _ ->
                toPromotionLevel(rs)
            }
        } else {
            namedParameterJdbcTemplate!!.query(
                """
                    SELECT *
                    FROM promotion_levels
                    WHERE name ILIKE :name
                """,
                mapOf("name" to "%$token%")
            ) { rs, _ ->
                toPromotionLevel(rs)
            }
        }

    override fun toPromotionLevel(rs: ResultSet, branch: Branch?) = PromotionLevel(
        id = id(rs),
        name = rs.getString("name"),
        description = rs.getString("description"),
        branch = branch ?: branchJdbcRepositoryAccessor.getBranch(id(rs, "branchid")),
        isImage = !rs.getString("imagetype").isNullOrBlank(),
        signature = readSignature(rs),
    )
}