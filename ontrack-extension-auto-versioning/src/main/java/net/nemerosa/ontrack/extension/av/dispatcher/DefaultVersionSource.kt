package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.Project
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
        buildDisplayNameService.findBuildByDisplayName(
            project = sourceProject,
            name = version,
            onlyDisplayName = false,
        )

}