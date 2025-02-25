package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ValidationStamp
import java.sql.ResultSet

interface ValidationStampJdbcRepositoryAccessor {

    fun toValidationStamp(rs: ResultSet, branch: Branch? = null): ValidationStamp

}