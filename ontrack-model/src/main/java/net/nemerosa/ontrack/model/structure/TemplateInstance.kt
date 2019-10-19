package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Definition of a branch instance
 */
open class TemplateInstance(
        /**
         * Template definition (branch ID)
         */
        val templateDefinitionId: ID,
        /**
         * List of parameter values
         */
        val parameterValues: List<TemplateParameterValue>
) {
    val parameterMap: Map<String, String>
        @JsonIgnore
        get() = parameterValues.associate { it.name to it.value }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TemplateInstance

        if (templateDefinitionId != other.templateDefinitionId) return false
        if (parameterValues != other.parameterValues) return false

        return true
    }

    override fun hashCode(): Int {
        var result = templateDefinitionId.hashCode()
        result = 31 * result + parameterValues.hashCode()
        return result
    }

}
