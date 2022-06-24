package net.nemerosa.ontrack.extension.dm.tse

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class TimeSincePromotionJdbcHelper(
    dataSource: DataSource,
) : AbstractJdbcRepository(dataSource), TimeSincePromotionHelper {

    override fun findOldestBuildAfterBuild(ref: Build): Int? =
        getFirstItem(
            """
                SELECT ID
                FROM BUILDS b
                WHERE BRANCHID = :branchId
                AND ID > :refId
                ORDER BY ID ASC
                LIMIT 1
            """,
            params("branchId", ref.branch.id()).addValue("refId", ref.id()),
            Int::class.java
        )

    override fun findOldestPromotionAfterBuild(ref: Build, promotion: PromotionLevel): Int? =
        getFirstItem(
            """
                    SELECT PR.ID
                    FROM BUILDS B
                    INNER JOIN PROMOTION_RUNS PR ON PR.BUILDID = B.ID
                    WHERE B.BRANCHID = :branchId
                    AND PR.PROMOTIONLEVELID = :promotionId
                    AND B.ID > :refId
                    ORDER BY PR.ID ASC
                    LIMIT 1 
                """,
            params("branchId", ref.branch.id()).addValue("refId", ref.id()).addValue("promotionId", promotion.id()),
            Int::class.java
        )
}