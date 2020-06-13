package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescriptionState
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class ProjectMutations(
        private val structureService: StructureService
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
            /**
             * Creating a project
             */
            simpleMutation(
                    CREATE_PROJECT, "Creates a new project", NameDescriptionState::class,
                    "project", "Created project", Project::class
            ) { input ->
                structureService.newProject(Project.of(input))
            },
            /**
             * Updating a project
             */
            simpleMutation(
                    UPDATE_PROJECT, "Updates an existing project", UpdateProjectInput::class,
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
            unitMutation<DeleteProjectInput>(DELETE_PROJECT, "Deletes an existing project") { input ->
                structureService.deleteProject(ID(input.id))
            },
            /**
             * Disables a project
             */
            simpleMutation(
                    DISABLE_PROJECT, "Disables an existing project", DisableProjectInput::class,
                    "project", "Updated project", Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                structureService.disableProject(project)
            },
            /**
             * Enables a project
             */
            simpleMutation(
                    ENABLE_PROJECT, "Enables an existing project", EnableProjectInput::class,
                    "project", "Updated project", Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                structureService.enableProject(project)
            }
            // TODO Mark a project as favourite
            // TODO Unmark a project as favourite
    )

    companion object {
        const val ENABLE_PROJECT = "enableProject"
        const val DISABLE_PROJECT = "disableProject"
        const val DELETE_PROJECT = "deleteProject"
        const val UPDATE_PROJECT = "updateProject"
        const val CREATE_PROJECT = "createProject"
    }

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
