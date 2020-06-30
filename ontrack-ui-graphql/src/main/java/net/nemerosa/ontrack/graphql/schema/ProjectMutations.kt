package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Component
class ProjectMutations(
        private val structureService: StructureService,
        private val projectFavouriteService: ProjectFavouriteService
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
                validateInput(state)
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
            },
            /*
             * Mark a project as favourite
             */
            simpleMutation(
                    FAVOURITE_PROJECT, "Marks a project as favourite", FavouriteProjectInput::class,
                    "project", "Updated project", Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                projectFavouriteService.setProjectFavourite(project, true)
                structureService.getProject(ID(input.id))
            },
            /*
             * Unmark a project as favourite
             */
            simpleMutation(
                    UNFAVOURITE_PROJECT, "Unmarks a project as favourite", UnfavouriteProjectInput::class,
                    "project", "Updated project", Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                projectFavouriteService.setProjectFavourite(project, false)
                structureService.getProject(ID(input.id))
            }
    )

    companion object {
        const val ENABLE_PROJECT = "enableProject"
        const val DISABLE_PROJECT = "disableProject"
        const val DELETE_PROJECT = "deleteProject"
        const val UPDATE_PROJECT = "updateProject"
        const val CREATE_PROJECT = "createProject"
        const val FAVOURITE_PROJECT = "favouriteProject"
        const val UNFAVOURITE_PROJECT = "unfavouriteProject"
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

data class FavouriteProjectInput(
        @APIDescription("Project ID")
        val id: Int
)

data class UnfavouriteProjectInput(
        @APIDescription("Project ID")
        val id: Int
)
