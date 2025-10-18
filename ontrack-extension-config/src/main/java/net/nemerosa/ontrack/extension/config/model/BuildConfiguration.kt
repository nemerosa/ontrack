package net.nemerosa.ontrack.extension.config.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.config.extensions.CIConfigExtensionService
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Branch configuration")
data class BuildConfiguration(
    override val properties: List<PropertyConfiguration> = emptyList(),
    override val extensions: List<ExtensionConfiguration> = emptyList(),
) : PropertiesConfiguration, ExtensionsConfiguration {

    @JsonIgnore
    fun isNotEmpty(): Boolean = properties.isNotEmpty() || extensions.isNotEmpty()

    fun merge(build: BuildConfiguration, ciConfigExtensionService: CIConfigExtensionService) = BuildConfiguration(
        properties = this.properties + build.properties,
        extensions = ciConfigExtensionService.merge(this.extensions, build.extensions),
    )
}
