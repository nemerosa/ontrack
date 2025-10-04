package net.nemerosa.ontrack.extension.config.ci.validations

import org.springframework.stereotype.Component

@Component
class ValidationDataTypeAliasServiceImpl(
    validationDataTypeAliases: List<ValidationDataTypeAlias>,
) : ValidationDataTypeAliasService {

    private val index = validationDataTypeAliases.associateBy { it.alias }

    override fun findValidationDataTypeAlias(type: String): ValidationDataTypeAlias? = index[type]
}