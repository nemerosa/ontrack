package net.nemerosa.ontrack.graphql.schema

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.exceptions.ProjectNameAlreadyDefinedException
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

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
                name = CREATE_PROJECT,
                description = "Creates a new project",
                input = CreateProjectInput::class,
                outputName = "project",
                outputDescription = "Created project",
                outputType = Project::class
            ) { input ->
                structureService.newProject(Project.of(input.toNameDescriptionState()))
            },
            /**
             * Creating a project or getting it if it already exists
             */
            simpleMutation(
                name = CREATE_PROJECT_OR_GET,
                description = "Creates a new project or gets it if it already exists",
                input = CreateProjectOrGetInput::class,
                outputName = "project",
                outputDescription = "Created or existing project",
                outputType = Project::class
            ) { input ->
                createProjectOrGet(input)
            },
            /**
             * Updating a project
             */
            simpleMutation(
                name = UPDATE_PROJECT,
                description = "Updates an existing project",
                input = UpdateProjectInput::class,
                outputName = "project",
                outputDescription = "Updated project",
                outputType = Project::class
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
                name = DISABLE_PROJECT,
                description = "Disables an existing project",
                input = DisableProjectInput::class,
                outputName = "project",
                outputDescription = "Updated project",
                outputType = Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                structureService.disableProject(project)
            },
            /**
             * Enables a project
             */
            simpleMutation(
                name = ENABLE_PROJECT,
                description = "Enables an existing project",
                input = EnableProjectInput::class,
                outputName = "project",
                outputDescription = "Updated project",
                outputType = Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                structureService.enableProject(project)
            },
            /*
             * Mark a project as favourite
             */
            simpleMutation(
                name = FAVOURITE_PROJECT,
                description = "Marks a project as favourite",
                input = FavouriteProjectInput::class,
                outputName = "project",
                outputDescription = "Updated project",
                outputType = Project::class
            ) { input ->
                val project = structureService.getProject(ID(input.id))
                projectFavouriteService.setProjectFavourite(project, true)
                structureService.getProject(ID(input.id))
            },
            /*
             * Unmark a project as favourite
             */
            simpleMutation(
                name = UNFAVOURITE_PROJECT,
                description = "Unmarks a project as favourite",
                input = UnfavouriteProjectInput::class,
                outputName = "project",
                outputDescription = "Updated project",
                outputType = Project::class
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
