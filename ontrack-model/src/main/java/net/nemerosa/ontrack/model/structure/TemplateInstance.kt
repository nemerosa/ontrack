package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Definition of a branch instance
 */
data class TemplateInstance(
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
}
