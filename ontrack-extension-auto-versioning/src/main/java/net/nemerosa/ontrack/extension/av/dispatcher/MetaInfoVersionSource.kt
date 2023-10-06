package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.MetaInfoPropertyType
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class MetaInfoVersionSource(
    private val propertyService: PropertyService,
) : VersionSource {

    override val id: String = "metaInfo"

    override fun getVersion(build: Build, config: String?): String {
        val mi = propertyService.getPropertyValue(build, MetaInfoPropertyType::class.java)
            ?: throw VersionSourceNoVersionException("Build ${build.id} (${build.entityDisplayName}) has no meta information")

        val (name, category) = if (config.isNullOrBlank()) {
            throw VersionSourceNoVersionException("metaInfo version source requires a parameter for the key to look for")
        } else if (config.contains("/")) {
            config.substringAfter("/") to config.substringBefore("/")
        } else {
            // Name only
            config to null
        }

        val item = mi.findMetaInfoItems(name, category).firstOrNull()
            ?: throw VersionSourceNoVersionException(
                "Cannot find meta information on " +
                        "build ${build.id} (${build.entityDisplayName}) with key $config"
            )

        return item.value
            ?.takeIf { it.isNotBlank() }
            ?: throw VersionSourceNoVersionException(
                "Build ${build.id} (${build.entityDisplayName}) has some meta information for key $config but this does not contain any value"
            )
    }
}