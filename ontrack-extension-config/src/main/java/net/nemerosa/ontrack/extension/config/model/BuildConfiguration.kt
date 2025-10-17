package net.nemerosa.ontrack.extension.config.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Branch configuration")
data class BuildConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
    val autoVersioningCheck: Boolean? = null,
) : PropertiesConfiguration {

    @JsonIgnore
    fun isNotEmpty(): Boolean = properties.isNotEmpty() || autoVersioningCheck != null

    fun merge(build: BuildConfiguration) = BuildConfiguration(
        properties = this.properties + build.properties,
        autoVersioningCheck = build.autoVersioningCheck ?: this.autoVersioningCheck,
    )
}
