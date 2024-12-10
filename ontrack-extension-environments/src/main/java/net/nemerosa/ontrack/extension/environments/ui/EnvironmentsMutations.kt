package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.EnvironmentsLicense
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component

@Component
class EnvironmentsMutations(
    private val environmentsLicense: EnvironmentsLicense,
    private val environmentService: EnvironmentService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "createEnvironment",
            description = "Creates a new environment",
            input = CreateEnvironmentInput::class,
            outputName = "environment",
            outputDescription = "Created environment",
            outputType = Environment::class,
        ) { input ->
            environmentsLicense.checkEnvironmentFeatureEnabled()
            Environment(
                name = input.name,
                order = input.order,
                description = input.description,
                tags = input.tags ?: emptyList(),
                image = false,
            ).apply {
                environmentService.save(this)
            }
        },
        unitMutation(
            name = "deleteEnvironment",
            description = "Deletes an existing environment",
            input = DeleteEnvironmentInput::class,
        ) { input ->
            environmentsLicense.checkEnvironmentFeatureEnabled()
            val env = environmentService.getById(input.id)
            environmentService.delete(env)
        },
    )
}

data class CreateEnvironmentInput(
    @APIDescription("Unique name for the environment")
    val name: String,
    @APIDescription("Order to the environment, used to sort them from lower environments to higher ones")
    val order: Int,
    @APIDescription("Description for the environment")
    val description: String?,
    @APIDescription("Tags for the environment")
    @ListRef
    val tags: List<String>?,
)

data class DeleteEnvironmentInput(
    @APIDescription("ID of the environment to delete")
    val id: String,
)
