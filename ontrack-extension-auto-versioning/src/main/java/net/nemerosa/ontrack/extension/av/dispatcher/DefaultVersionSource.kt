package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import org.springframework.stereotype.Component

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
}