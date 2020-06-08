package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.simpleMutation
import net.nemerosa.ontrack.graphql.support.unitMutation
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescriptionState
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class ProjectMutations(
        private val structureService: StructureService
) : MutationProvider {

    override val mutations: List<Mutation> = listOf(
            /**
             * Creating a project
             */
            simpleMutation(
                    "createProject", "Creates a new project", NameDescriptionState::class,
                    "project", "Created project", Project::class
            ) { input ->
                structureService.newProject(Project.of(input))
            },
            /**
             * Updating a project
             */
            simpleMutation(
                    "updateProject", "Updates an existing project", UpdateProjectInput::class,
                    "project", "Updated project", Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                val state = NameDescriptionState(
                        name = input.name ?: project.name,
                        description = input.description ?: project.description,
                        isDisabled = input.disabled ?: project.isDisabled
                )
                // TODO Validation of the state
                val updatedProject = project.update(state)
                structureService.saveProject(updatedProject)
                updatedProject
            },
            /**
             * Deleting a project
             */
            unitMutation<DeleteProjectInput>("deleteProject", "Deletes an existing project") { input ->
                structureService.deleteProject(ID(input.id))
            },
            /**
             * Disables a project
             */
            simpleMutation(
                    "disableProject", "Disables an existing project", DisableProjectInput::class,
                    "project", "Updated project", Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                structureService.disableProject(project)
            },
            /**
             * Enables a project
             */
            simpleMutation(
                    "enableProject", "Enables an existing project", EnableProjectInput::class,
                    "project", "Updated project", Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                structureService.enableProject(project)
            }
    )

}

data class UpdateProjectInput(
        @APIDescription("Project ID")
        val id: Int,
        @APIDescription("Project name (leave null to not change)")
        val name: String?,
        @APIDescription("Project description (leave null to not change)")
        val description: String?,
        @APIDescription("Project state (leave null to not change)")
        val disabled: Boolean?
)

data class DeleteProjectInput(
        @APIDescription("Project ID")
        val id: Int
)

data class DisableProjectInput(
        @APIDescription("Project ID")
        val id: Int
)

data class EnableProjectInput(
        @APIDescription("Project ID")
        val id: Int
)
