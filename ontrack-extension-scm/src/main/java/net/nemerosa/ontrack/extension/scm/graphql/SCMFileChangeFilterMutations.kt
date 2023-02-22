package net.nemerosa.ontrack.extension.scm.graphql

import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilter
import net.nemerosa.ontrack.extension.scm.service.SCMFileChangeFilterService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class SCMFileChangeFilterMutations(
    private val securityService: SecurityService,
    private val structureService: StructureService,
    private val scmFileChangeFilterService: SCMFileChangeFilterService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        unitMutation<ShareSCMFileChangeFilterInput>(
            name = "shareSCMFileChangeFilter",
            description = "Shares or updates a filter at project level",
        ) { input ->
            val project = structureService.getProject(ID.of(input.projectId))
            securityService.checkProjectFunction(project, ProjectConfig::class.java)
            scmFileChangeFilterService.save(
                project, SCMFileChangeFilter(
                    input.name,
                    input.patterns,
                )
            )
        },
    )

}