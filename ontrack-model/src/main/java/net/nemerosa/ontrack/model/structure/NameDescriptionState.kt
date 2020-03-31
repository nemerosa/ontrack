package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.NAME
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.NAME_MESSAGE_SUFFIX
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern


data class NameDescriptionState(
        @get:NotNull(message = "The name is required.")
        @get:Pattern(regexp = NAME, message = "The name $NAME_MESSAGE_SUFFIX")
        val name: String,
        val description: String?,
        @get:JsonProperty("disabled")
        val isDisabled: Boolean
)
