package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.ordering.VersionUtils
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.templating.TemplatingSource
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import org.springframework.stereotype.Component

@Component
@APIDescription("Extract a version string from a branch name")
@Documentation(BranchVersionTemplatingSourceParameters::class)
@DocumentationExampleCode($$"${branch.version}")
class BranchVersionTemplatingSource(
    private val branchDisplayNameService: BranchDisplayNameService,
) : TemplatingSource {

    override val types: Set<ProjectEntityType> = setOf(ProjectEntityType.BRANCH)
    override val field: String = "version"

    override fun render(entity: ProjectEntity, config: TemplatingSourceConfig, renderer: EventRenderer): String {
        val branch = entity as Branch
        // Parameters
        val policy = config.getString(BranchVersionTemplatingSourceParameters::policy.name)
            ?.takeIf { it.isNotBlank() }
            ?.let { BranchNamePolicy.valueOf(it) }
            ?: BranchNamePolicy.DISPLAY_NAME_OR_NAME
        val default = config.getString(BranchVersionTemplatingSourceParameters::default.name) ?: ""
        // Branch name
        val name = branchDisplayNameService.getBranchDisplayName(branch, policy)
        // Extracting the version
        val version = VersionUtils.getVersionText(VersionUtils.semVerSuffixRegex, name)
        // Default value
        return version ?: default
    }
}