package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.NAME
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.NAME_MESSAGE_SUFFIX

data class NameDescriptionState(
        @get:NotNull(message = "The name is required.")
        @get:Pattern(regexp = NAME, message = "The name $NAME_MESSAGE_SUFFIX")
        val name: String,
        val description: String?,
        @get:JsonProperty("disabled")
        val isDisabled: Boolean
)
