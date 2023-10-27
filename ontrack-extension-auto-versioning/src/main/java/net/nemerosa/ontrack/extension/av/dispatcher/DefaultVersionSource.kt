package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * Looks first using the display name of the build
 * and then using the name of the build.
 *
 * Configuration parameter is not used.
 */
@Component
class DefaultVersionSource(
    private val buildDisplayNameService: BuildDisplayNameService,
    private val structureService: StructureService,
) : VersionSource {

    companion object {
        const val ID = "default"
    }

    override val id: String = "default"

    override fun getVersion(build: Build, config: String?): String =
        buildDisplayNameService.getEligibleBuildDisplayName(build) ?: throw VersionSourceNoVersionException(
            "Build ${build.id} (${build.entityDisplayName}) was promoted, " +
                    "but is not eligible to auto versioning because no version was returned. " +
                    "This can typically be due to the fact that its project requires a label " +
                    "and the build has none."
        )

    override fun getBuildFromVersion(sourceProject: Project, config: String?, version: String): Build? =
        // Looking first with release property
        structureService.buildSearch(
            projectId = sourceProject.id,
            form = BuildSearchForm(
                maximumCount = 1,
                property = ReleasePropertyType::class.java.name,
                propertyValue = version,
            )
        ).firstOrNull()
        // ... then by name
            ?: structureService.buildSearch(
                projectId = sourceProject.id,
                form = BuildSearchForm(
                    maximumCount = 1,
                    buildName = version,
                    buildExactMatch = true,
                )
            ).firstOrNull()

}