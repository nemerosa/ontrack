package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.getBuildDisplayNameOrName
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
        buildDisplayNameService.getBuildDisplayNameOrName(build)

    override fun getBuildFromVersion(sourceProject: Project, config: String?, version: String): Build? =
        buildDisplayNameService.findBuildByDisplayName(
            project = sourceProject,
            name = version,
            onlyDisplayName = false,
        )

}