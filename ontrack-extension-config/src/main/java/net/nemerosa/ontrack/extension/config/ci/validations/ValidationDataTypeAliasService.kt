package net.nemerosa.ontrack.extension.config.ci.validations

interface ValidationDataTypeAliasService {
    fun findValidationDataTypeAlias(type: String): ValidationDataTypeAlias?
}