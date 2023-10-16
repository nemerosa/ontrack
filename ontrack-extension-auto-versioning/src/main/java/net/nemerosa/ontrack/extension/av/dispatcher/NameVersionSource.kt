package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class NameVersionSource(
    private val structureService: StructureService,
) : VersionSource {

    override val id: String = "name"

    override fun getVersion(build: Build, config: String?): String = build.name

    override fun getBuildFromVersion(sourceProject: Project, config: String?, version: String): Build? =
        structureService.buildSearch(
            projectId = sourceProject.id,
            form = BuildSearchForm(
                maximumCount = 1,
                buildName = version,
                buildExactMatch = true,
            )
        ).firstOrNull()
    
}