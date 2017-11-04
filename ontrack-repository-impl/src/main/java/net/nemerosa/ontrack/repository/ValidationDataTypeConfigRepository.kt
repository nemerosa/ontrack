package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.ValidationDataTypeConfig
import java.sql.ResultSet

interface ValidationDataTypeConfigRepository {

    fun <C> readValidationDataTypeConfig(rs: ResultSet): ValidationDataTypeConfig<C>?

}
