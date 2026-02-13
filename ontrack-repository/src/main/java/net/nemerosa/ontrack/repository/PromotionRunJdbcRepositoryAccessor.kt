package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import java.sql.ResultSet

interface PromotionRunJdbcRepositoryAccessor {

    fun getPromotionRun(id: ID, promotionLevel: PromotionLevel? = null, build: Build? = null): PromotionRun

    fun toPromotionRun(rs: ResultSet, promotionLevel: PromotionLevel? = null, build: Build? = null): PromotionRun

}