package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class PromotionRunJdbcRepository(
    dataSource: DataSource,
    private val promotionLevelJdbcRepositoryAccessor: PromotionLevelJdbcRepositoryAccessor,
    private val buildJdbcRepositoryAccessor: BuildJdbcRepositoryAccessor,
) : AbstractJdbcRepository(dataSource), PromotionRunRepository, PromotionRunJdbcRepositoryAccessor {

    override fun toPromotionRun(rs: ResultSet): PromotionRun {
        val build = buildJdbcRepositoryAccessor.getBuild(id(rs, "buildid"))
        return PromotionRun(
            id = id(rs),
            build = build,
            promotionLevel = promotionLevelJdbcRepositoryAccessor.getPromotionLevel(
                id(rs, "promotionlevelid"),
                build.branch,
            ),
            signature = readSignature(rs),
            description = rs.getString("description"),
        )
    }

    override fun getLastPromotionRunForProject(project: Project, promotionName: String): PromotionRun? =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT PR.*
                 FROM PROMOTION_RUNS PR
                 INNER JOIN PROMOTION_LEVELS P ON P.ID = PR.PROMOTIONLEVELID
                 INNER JOIN BRANCHES B ON B.ID = P.BRANCHID
                 WHERE B.PROJECTID = :projectId
                 AND P.NAME = :promotionName
                 ORDER BY PR.ID DESC
                 LIMIT 1
            """,
            mapOf(
                "projectId" to project.id(),
                "promotionName" to promotionName,
            )
        ) { rs: ResultSet, _ ->
            toPromotionRun(rs)
        }.firstOrNull()
}