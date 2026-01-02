package net.nemerosa.ontrack.model.structure

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.NAME_MESSAGE_SUFFIX

class ValidationStampInput(
    @get:NotBlank(message = "The name is required.")
    @get:Pattern(regexp = ValidationStamp.NAME_REGEX, message = "The name $NAME_MESSAGE_SUFFIX")
    val name: String,
    val description: String?,
    val dataType: ServiceConfiguration?
) {
    fun asNameDescription() = NameDescription.nd(name, description)
}
