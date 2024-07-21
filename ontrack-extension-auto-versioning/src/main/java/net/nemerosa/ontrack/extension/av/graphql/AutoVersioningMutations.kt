package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.validation.AutoVersioningValidationService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class AutoVersioningMutations(
    private val structureService: StructureService,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
    private val autoVersioningValidationService: AutoVersioningValidationService,
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
        },

        simpleMutation(
            name = "setAutoVersioningConfigByName",
            description = "Sets the auto versioning configuration for a branch identified by name",
            input = SetAutoVersioningConfigByNameInput::class,
            outputName = "branch",
            outputDescription = "Configured branch",
            outputType = Branch::class
        ) { input ->
            val branch = structureService.findBranchByName(input.project, input.branch)
                .getOrNull()
                ?: throw BranchNotFoundException(input.project, input.branch)
            autoVersioningConfigurationService.setupAutoVersioning(
                branch = branch,
                config = AutoVersioningConfig(
                    configurations = input.configurations
                )
            )
            branch
        },

        unitMutation(
            name = "checkAutoVersioning",
            description = "Checks the status of the auto versioning compared to the latest versions and creates an appropriate validation",
            input = CheckAutoVersioningInput::class,
        ) { input ->
            val build = structureService.findBuildByName(
                input.project,
                input.branch,
                input.build
            ).getOrNull()
            if (build != null) {
                autoVersioningValidationService.checkAndValidate(build)
            }
        },

        unitMutation(
            name = "deleteAutoVersioningConfig",
            description = "Deletes the auto-versioning configuration from a branch",
            input = DeleteAutoVersioningConfigInput::class,
        ) { input ->
            val branch = structureService.getBranch(ID.of(input.branchId))
            autoVersioningConfigurationService.setupAutoVersioning(branch, null)
        },

        )
}

