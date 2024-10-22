package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.checkEnvironmentFeatureEnabled
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import org.springframework.stereotype.Component

@Component
class EnvironmentsMutations(
    private val licenseControlService: LicenseControlService,
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
            licenseControlService.checkEnvironmentFeatureEnabled()
            Environment(
                name = input.name,
                order = input.order,
                description = input.description,
                tags = input.tags,
            ).apply {
                environmentService.save(this)
            }
        }
    )
}

data class CreateEnvironmentInput(
    val name: String,
    val description: String,
    val order: Int,
    @ListRef
    val tags: List<String>,
)
