package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.ValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationDataTypeConfig
import net.nemerosa.ontrack.model.structure.ValidationDataTypeService
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.sql.DataSource

@Repository
class ValidationDataTypeConfigJdbcRepository(
        dataSource: DataSource,
        private val validationDataTypeService: ValidationDataTypeService
) : AbstractJdbcRepository(dataSource), ValidationDataTypeConfigRepository {

    override fun <C> readValidationDataTypeConfig(rs: ResultSet): ValidationDataTypeConfig<C>? {
        val id = rs.getString("DATA_TYPE_ID")
        val json = readJson(rs, "DATA_TYPE_CONFIG")
        if (StringUtils.isBlank(id) || json == null) {
            return null
        } else {
            val validationDataType: ValidationDataType<C, Any>? = validationDataTypeService.getValidationDataType(id)
            if (validationDataType != null) {
                // Parsing
                val config = validationDataType.configFromJson(json)
                // OK
                return ValidationDataTypeConfig(
                        validationDataType.descriptor,
                        config
                )
            } else {
                logger.warn("Cannot find validation data type for ID = " + id)
                return null
            }
        }
    }
}