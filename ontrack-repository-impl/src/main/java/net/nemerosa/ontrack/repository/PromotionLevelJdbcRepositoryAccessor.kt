package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.PromotionLevel
import java.sql.ResultSet

interface PromotionLevelJdbcRepositoryAccessor {

    fun toPromotionLevel(rs: ResultSet): PromotionLevel

}