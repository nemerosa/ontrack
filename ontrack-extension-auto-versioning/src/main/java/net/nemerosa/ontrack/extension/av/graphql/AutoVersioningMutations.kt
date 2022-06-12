package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class AutoVersioningMutations(
    private val structureService: StructureService,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(

        simpleMutation(
            name = "setAutoVersioningConfig",
            description = "Sets the auto versioning configuration for a branch",
            input = SetAutoVersioningConfigInput::class,
            outputName = "branch",
            outputDescription = "Configured branch",
            outputType = Branch::class
        ) { input ->
            val branch = structureService.getBranch(ID.of(input.branchId))
            autoVersioningConfigurationService.setupAutoVersioning(
                branch = branch,
                config = AutoVersioningConfig(
                    configurations = input.configurations
                )
            )
            branch
        }

    )
}

