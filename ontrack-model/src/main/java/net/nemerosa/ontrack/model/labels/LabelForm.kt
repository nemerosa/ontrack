package net.nemerosa.ontrack.model.labels

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

class LabelForm(
        val category: String?,
        @NotNull(message = "The name is required.")
        val name: String,
        val description: String?,
        @NotNull(message = "The color is required.")
        @Pattern(regexp = "#[a-fA-F0-9]{6}", message = "The color must comply with format #[a-fA-F0-9]{6}")
        val color: String
)
