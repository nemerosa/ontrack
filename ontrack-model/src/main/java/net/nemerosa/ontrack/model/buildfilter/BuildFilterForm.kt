package net.nemerosa.ontrack.model.buildfilter

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIIgnore
import net.nemerosa.ontrack.model.form.Form

data class BuildFilterForm(
        @APIDescription("FQCN for the build filter provider")
        val type: Class<out BuildFilterProvider<*>>,
        @APIDescription("Display name for the filter")
        val typeName: String,
        @APIDescription("If the filter is predefined and cannot be configured.")
        val isPredefined: Boolean,
        @APIDescription("Form for the filter.")
        val form: Form,
) {

    fun with(data: Map<String, String>) = BuildFilterForm(
            type,
            typeName,
            isPredefined,
            form.fill(data)
    )

}