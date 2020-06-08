package net.nemerosa.ontrack.graphql.schema

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.listField
import net.nemerosa.ontrack.graphql.support.mutationInput
import net.nemerosa.ontrack.graphql.support.simpleMutation
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
            simpleMutation("createProject", "Creates a new project",
                    NameDescriptionState::class,
                    "project", "Created project", Project::class
            ) { input ->
                structureService.newProject(Project.of(input))
            },
            /**
             * Updating a project
             */
            simpleMutation("updateProject", "Updates an existing project",
                    UpdateProjectInput::class,
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
            object : Mutation {
                override val name: String = "deleteProject"
                override val description: String = "Deletes an existing project"

                override val inputFields: List<GraphQLInputObjectField> = GraphQLBeanConverter.asInputFields(DeleteProjectInput::class)

                override val outputFields: List<GraphQLFieldDefinition> = listOf(
                        listField("projects", "List of remaining projects") {
                            structureService.projectList
                        }
                )

                override fun fetch(env: DataFetchingEnvironment): Any {
                    val input = mutationInput<DeleteProjectInput>(env)
                    structureService.deleteProject(ID(input.id))
                    return Unit // Not used
                }
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
