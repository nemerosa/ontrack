package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.BuildLinkDisplayProperty
import net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType
import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * Default way of getting the "name" of a build.
 *
 * | | No project BuildLinkDisplayProperty property | BuildLinkDisplayProperty - false | BuildLinkDisplayProperty - true |
 * |-|---|---|---|
 * | No build ReleaseProperty | Build name | Build name | (x) Error |
 * | ReleaseProperty set | Build release | Build name | Build release |
 */
@Component
class DefaultVersionSource(
    private val buildDisplayNameService: BuildDisplayNameService,
    private val propertyService: PropertyService,
) : VersionSource {

    companion object {
        const val ID = "default"
    }

    override val id: String = "default"

    override fun getVersion(build: Build, config: String?): String {
        val displayProperty: BuildLinkDisplayProperty? =
            propertyService.getProperty(build.project, BuildLinkDisplayPropertyType::class.java).value
        val releaseProperty: ReleaseProperty? =
            propertyService.getProperty(build, ReleasePropertyType::class.java).value
        return if (displayProperty == null) {
            releaseProperty?.name ?: build.name
        } else if (displayProperty.useLabel) {
            releaseProperty?.name ?: throw VersionSourceNoVersionException(
                """Build ${build.defaultDisplayName} has no release property but this is marked as required by the project."""
            )
        } else {
            build.name
        }
    }

    override fun getBuildFromVersion(sourceProject: Project, config: String?, version: String): Build? =
        buildDisplayNameService.findBuildByDisplayName(
            project = sourceProject,
            name = version,
            onlyDisplayName = false,
        )

}