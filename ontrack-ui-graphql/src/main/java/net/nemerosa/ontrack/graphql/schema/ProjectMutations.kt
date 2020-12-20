package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.exceptions.ProjectNameAlreadyDefinedException
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Component
class ProjectMutations(
        private val structureService: StructureService,
        private val securityService: SecurityService,
        private val projectFavouriteService: ProjectFavouriteService
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
            /**
             * Creating a project
             */
            simpleMutation(
                    CREATE_PROJECT, "Creates a new project", CreateProjectInput::class,
                    "project", "Created project", Project::class
            ) { input ->
                structureService.newProject(Project.of(input.toNameDescriptionState()))
            },
            /**
             * Creating a project or getting it if it already exists
             */
            simpleMutation(
                    CREATE_PROJECT_OR_GET, "Creates a new project or gets it if it already exists", CreateProjectOrGetInput::class,
                    "project", "Created or existing project", Project::class
            ) { input ->
                createProjectOrGet(input)
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

    private fun createProjectOrGet(input: CreateProjectOrGetInput): Project {
        // Gets the existing project using admin rights
        // since a project might exist with the same name
        // but not be accessible
        val existing = securityService.asAdmin {
            structureService.findProjectByName(input.name).getOrNull()
        }
        // If the project exists & is accessible, just returns it
        return if (existing != null) {
            if (securityService.isProjectFunctionGranted(existing, ProjectView::class.java)) {
                existing
            } else {
                throw ProjectNameAlreadyDefinedException(input.name)
            }
        } else {
            structureService.newProject(Project.of(input.toNameDescriptionState()))
        }
    }

    companion object {
        const val ENABLE_PROJECT = "enableProject"
        const val DISABLE_PROJECT = "disableProject"
        const val DELETE_PROJECT = "deleteProject"
        const val UPDATE_PROJECT = "updateProject"
        const val CREATE_PROJECT = "createProject"
        const val CREATE_PROJECT_OR_GET = "createProjectOrGet"
        const val FAVOURITE_PROJECT = "favouriteProject"
        const val UNFAVOURITE_PROJECT = "unfavouriteProject"
    }

}

data class CreateProjectInput(
        @get:NotNull(message = "The name is required.")
        @get:Pattern(regexp = NameDescription.NAME, message = "The name ${NameDescription.NAME_MESSAGE_SUFFIX}")
        @APIDescription("Project name")
        val name: String,
        @APIDescription("Project description")
        val description: String?,
        @APIDescription("Project state, null for not disabled")
        val disabled: Boolean?
) {
    fun toNameDescriptionState() = NameDescriptionState(
            name = name,
            description = description,
            isDisabled = disabled ?: false
    )
}

data class CreateProjectOrGetInput(
        @get:NotNull(message = "The name is required.")
        @get:Pattern(regexp = NameDescription.NAME, message = "The name ${NameDescription.NAME_MESSAGE_SUFFIX}")
        @APIDescription("Project name")
        val name: String,
        @APIDescription("Project description")
        val description: String?,
        @APIDescription("Project state, null for not disabled")
        val disabled: Boolean?
) {
    fun toNameDescriptionState() = NameDescriptionState(
            name = name,
            description = description,
            isDisabled = disabled ?: false
    )
}

data class UpdateProjectInput(
        @APIDescription("Project ID")
        val id: Int,
        @get:Pattern(regexp = NameDescription.NAME, message = "The name ${NameDescription.NAME_MESSAGE_SUFFIX}")
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
