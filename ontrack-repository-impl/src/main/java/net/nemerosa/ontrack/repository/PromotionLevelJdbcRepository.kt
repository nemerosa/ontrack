package net.nemerosa.ontrack.repository

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

    override fun toPromotionLevel(rs: ResultSet) = PromotionLevel(
        id = id(rs),
        name = rs.getString("name"),
        description = rs.getString("description"),
        branch = branchJdbcRepositoryAccessor.getBranch(id(rs, "branchid")),
        isImage = !rs.getString("imagetype").isNullOrBlank(),
        signature = readSignature(rs),
    )
}