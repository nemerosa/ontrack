package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class AvTrailJdbcRepository(
    dataSource: DataSource
) : AbstractJdbcRepository(dataSource), AvTrailRepository {

    override fun saveForPromotionRun(
        run: PromotionRun,
        trail: StoredAutoVersioningTrail
    ) {
        namedParameterJdbcTemplate!!.update(
            """
                DELETE FROM AV_TRAIL
                WHERE PROMOTION_RUN_ID = :promotionRunId  
            """,
            mapOf("promotionRunId" to run.id())
        )
        for (branch in trail.branches) {
            namedParameterJdbcTemplate!!.update(
                """
                    INSERT INTO AV_TRAIL (
                        ID,
                        PROMOTION_RUN_ID,
                        PROJECT,
                        BRANCH,
                        CONFIGURATION,
                        ORDER_ID,
                        REJECTION_REASON
                    ) VALUES (
                        :id,
                        :promotionRunId,
                        :project,
                        :branch,
                        CAST(:configuration as JSONB),
                        :orderId,
                        :rejectionReason
                    )
                """.trimIndent(),
                mapOf(
                    "id" to branch.id,
                    "promotionRunId" to run.id(),
                    "project" to branch.project,
                    "branch" to branch.branch,
                    "configuration" to writeJson(branch.configuration),
                    "orderId" to branch.orderId,
                    "rejectionReason" to branch.rejectionReason
                )
            )
        }
    }

    override fun findByPromotionRun(run: PromotionRun, filter: AutoVersioningTrailFilter): StoredAutoVersioningTrail? =
        findBranchesByPromotionRun(
            run,
            filter = filter,
            offset = 0,
            size = Int.MAX_VALUE,
        ).takeIf { it.isNotEmpty() }
            ?.let { StoredAutoVersioningTrail(it) }

    override fun findBranchesByPromotionRun(
        run: PromotionRun,
        filter: AutoVersioningTrailFilter,
        offset: Int,
        size: Int
    ): List<StoredBranchTrail> {
        val sql = StringBuilder("SELECT *")
        val criterias = buildQuery(
            run, filter, sql,
            order = " ORDER BY PROMOTION_RUN_ID DESC OFFSET :offset LIMIT :size"
        ) + mapOf("offset" to offset, "size" to size)
        @Suppress("SqlSourceToSinkFlow")
        return namedParameterJdbcTemplate!!.query(
            sql.toString(),
            criterias
        ) { rs, _ ->
            StoredBranchTrail(
                id = rs.getString("ID"),
                project = rs.getString("PROJECT"),
                branch = rs.getString("BRANCH"),
                configuration = readJson(rs, "CONFIGURATION").parse<AutoVersioningSourceConfig>(),
                orderId = rs.getString("ORDER_ID"),
                rejectionReason = rs.getString("REJECTION_REASON")
            )
        }
    }

    override fun countBranchesByPromotionRun(
        run: PromotionRun,
        filter: AutoVersioningTrailFilter
    ): Int {
        val sql = StringBuilder("SELECT COUNT(ID)")
        val criterias = buildQuery(run, filter, sql)
        @Suppress("SqlSourceToSinkFlow")
        return namedParameterJdbcTemplate!!.queryForObject(
            sql.toString(),
            criterias,
            Int::class.java
        ) ?: 0
    }

    private fun buildQuery(
        run: PromotionRun,
        filter: AutoVersioningTrailFilter,
        sql: StringBuilder,
        order: String? = null,
    ): Map<String, Any?> {
        sql.append(
            """
                FROM AV_TRAIL
                WHERE PROMOTION_RUN_ID = :promotionRunId
            """.trimIndent()
        )

        val queries = mutableListOf<String>()
        val criterias = mutableMapOf<String, Any?>()
        criterias["promotionRunId"] = run.id()

        if (filter.onlyEligible) {
            queries += "REJECTION_REASON IS NULL"
        }

        if (!filter.projectName.isNullOrBlank()) {
            queries += "PROJECT ILIKE :projectName"
            criterias["projectName"] = "%${filter.projectName}%"
        }

        sql.append(queries.joinToString(" ") { " AND ($it)" })
        if (!order.isNullOrBlank()) {
            sql.append(order)
        }

        return criterias
    }

}