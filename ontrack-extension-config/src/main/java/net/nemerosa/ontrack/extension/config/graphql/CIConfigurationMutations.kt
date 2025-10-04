package net.nemerosa.ontrack.extension.config.graphql

import net.nemerosa.ontrack.extension.config.model.CIConfigurationService
import net.nemerosa.ontrack.extension.config.model.ConfigureBuildInput
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class CIConfigurationMutations(
    private val ciConfigurationService: CIConfigurationService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "configureBuild",
            description = "Configuration of a Yontrack build based on some input",
            input = ConfigureBuildInput::class,
            outputName = "build",
            outputDescription = "Configured build",
            outputType = Build::class,
        ) { input ->
            ciConfigurationService.configureBuild(
                config = input.config,
                ci = input.ci,
                scm = input.scm,
                env = input.env,
            )
        }
    )
}