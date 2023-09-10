package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PromotionLevel
import java.sql.ResultSet

interface PromotionLevelJdbcRepositoryAccessor {

    fun toPromotionLevel(rs: ResultSet, branch: Branch? = null): PromotionLevel

    fun getPromotionLevel(id: ID, branch: Branch? = null): PromotionLevel

}