package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class LabelOnlyVersionSource(
    private val buildDisplayNameService: BuildDisplayNameService,
    private val structureService: StructureService,
) : VersionSource {

    override val id: String = "labelOnly"

    override fun getVersion(build: Build, config: String?): String =
        buildDisplayNameService.getFirstBuildDisplayName(build) ?: throw VersionSourceNoVersionException(
            "Build ${build.id} (${build.entityDisplayName}) was promoted, " +
                    "but is not eligible to auto versioning because no version was returned. " +
                    "This can typically be due to the fact that its project requires a label " +
                    "and the build has none."
        )

    override fun getBuildFromVersion(sourceProject: Project, config: String?, version: String): Build? =
        structureService.buildSearch(
            projectId = sourceProject.id,
            form = BuildSearchForm(
                maximumCount = 1,
                property = ReleasePropertyType::class.java.name,
                propertyValue = version,
            )
        ).firstOrNull()

}