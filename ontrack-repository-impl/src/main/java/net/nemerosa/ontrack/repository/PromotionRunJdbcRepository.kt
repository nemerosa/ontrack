package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.*
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

    override fun getLastPromotionRunForBranch(branch: Branch, promotionName: String) =
        namedParameterJdbcTemplate!!.query(
            """
                SELECT PR.*
                 FROM PROMOTION_RUNS PR
                 INNER JOIN PROMOTION_LEVELS P ON P.ID = PR.PROMOTIONLEVELID
                 WHERE P.BRANCHID = :branchId
                 AND P.NAME = :promotionName
                 ORDER BY PR.ID DESC
                 LIMIT 1
            """,
            mapOf(
                "branchId" to branch.id(),
                "promotionName" to promotionName,
            )
        ) { rs: ResultSet, _ ->
            toPromotionRun(rs)
        }.firstOrNull()

    override fun isBuildPromoted(build: Build, promotionLevel: PromotionLevel): Boolean {
        return namedParameterJdbcTemplate!!.queryForList(
            """
                SELECT ID
                FROM PROMOTION_RUNS
                WHERE BUILDID = :buildId
                AND PROMOTIONLEVELID = :promotionLevelId
            """,
            mapOf(
                "buildId" to build.id(),
                "promotionLevelId" to promotionLevel.id(),
            ),
            Int::class.java
        ).isNotEmpty()
    }
}