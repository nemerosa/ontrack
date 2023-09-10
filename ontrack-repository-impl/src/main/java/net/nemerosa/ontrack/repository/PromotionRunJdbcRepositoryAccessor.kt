package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.PromotionRun
import java.sql.ResultSet

interface PromotionRunJdbcRepositoryAccessor {

    fun toPromotionRun(rs: ResultSet): PromotionRun

}