package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.simpleMutation
import net.nemerosa.ontrack.model.structure.NameDescriptionState
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class CreateProjectMutation(
        private val structureService: StructureService
) : MutationProvider {

    override val mutations: List<Mutation> = listOf(
            simpleMutation("createProject", "Creates a new project",
                    NameDescriptionState::class,
                    "project", "Created project", Project::class
            ) { input ->
                structureService.newProject(Project.of(input))
            }
    )

}