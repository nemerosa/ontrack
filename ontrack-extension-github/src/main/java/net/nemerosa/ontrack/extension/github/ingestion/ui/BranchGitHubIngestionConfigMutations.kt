package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigParser
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionConfig
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class BranchGitHubIngestionConfigMutations(
    private val structureService: StructureService,
    private val configService: ConfigService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "setBranchGitHubIngestionConfig",
            description = "Sets the GitHub Ingestion configuration programmatically",
            input = SetBranchGitHubIngestionConfigInput::class,
            outputName = "configuration",
            outputType = IngestionConfig::class,
            outputDescription = "GitHub ingestion configuration"
        ) { input ->
            val branch = structureService.getBranch(ID.of(input.branchId))
            val config = ConfigParser.parseYaml(input.yaml.trimIndent())
            configService.saveConfig(branch, config)
            config
        }
    )
}

@APIDescription("Input for updating a GitHub ingestion configuration")
data class SetBranchGitHubIngestionConfigInput(
    @APIDescription("ID of the branch to update")
    val branchId: Int,
    @APIDescription("Ingestion configuration as YAML. Indentation will be trimmed automatically.")
    val yaml: String,
)