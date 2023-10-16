package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.MetaInfoPropertyType
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class MetaInfoVersionSource(
    private val propertyService: PropertyService,
    private val structureService: StructureService,
) : VersionSource {

    override val id: String = "metaInfo"

    private fun parseConfigIntoNameAndCategory(config: String?): Pair<String, String?> =
        if (config.isNullOrBlank()) {
            throw VersionSourceNoVersionException("metaInfo version source requires a parameter for the key to look for")
        } else if (config.contains("/")) {
            config.substringAfter("/") to config.substringBefore("/")
        } else {
            // Name only
            config to null
        }

    override fun getVersion(build: Build, config: String?): String {
        val mi = propertyService.getPropertyValue(build, MetaInfoPropertyType::class.java)
            ?: throw VersionSourceNoVersionException("Build ${build.id} (${build.entityDisplayName}) has no meta information")

        val (name, category) = parseConfigIntoNameAndCategory(config)

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

    override fun getBuildFromVersion(sourceProject: Project, config: String?, version: String): Build? {
        val token = "$config:$version"
        return structureService.buildSearch(
            projectId = sourceProject.id,
            form = BuildSearchForm(
                maximumCount = 1,
                property = MetaInfoPropertyType::class.java.typeName,
                propertyValue = token,
            )
        ).firstOrNull()
    }
}