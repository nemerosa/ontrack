package net.nemerosa.ontrack.extension.config.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Branch configuration")
data class BuildConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
    override val extensions: List<ExtensionConfiguration> = emptyList(),
) : PropertiesConfiguration, ExtensionsConfiguration {

    @JsonIgnore
    fun isNotEmpty(): Boolean = properties.isNotEmpty()

    fun merge(build: BuildConfiguration) = BuildConfiguration(
        properties = this.properties + build.properties,
    )
}
