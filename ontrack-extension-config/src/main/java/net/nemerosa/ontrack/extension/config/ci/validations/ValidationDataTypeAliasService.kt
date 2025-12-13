package net.nemerosa.ontrack.extension.config.ci.validations

import net.nemerosa.ontrack.model.structure.ValidationDataTypeAlias

interface ValidationDataTypeAliasService {
    fun findValidationDataTypeAlias(type: String): ValidationDataTypeAlias?
}