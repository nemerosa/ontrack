package net.nemerosa.ontrack.extension.github.workflow

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.security.BuildConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class BuildGitHubWorkflowRunPropertyType(
    extensionFeature: GitHubExtensionFeature
) : AbstractPropertyType<BuildGitHubWorkflowRunProperty>(
    extensionFeature
) {

    override fun getName(): String = "GitHub Workflow Run"

    override fun getDescription(): String = "Link to the GitHub Workflow Run which created this build."

    override fun getSupportedEntityTypes() = setOf(ProjectEntityType.BUILD)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
        securityService.isProjectFunctionGranted(entity, BuildConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun getEditionForm(entity: ProjectEntity, value: BuildGitHubWorkflowRunProperty?): Form =
        Form.create()
            .with(
                Int.of(BuildGitHubWorkflowRunProperty::runId.name)
                    .label("ID")
                    .help("Unique ID of the workflow run")
                    .value(value?.runId)
            )
            .with(
                Url.of(BuildGitHubWorkflowRunProperty::url.name)
                    .label("URL")
                    .help("Link to the GitHub Workflow run")
                    .value(value?.url)
            )
            .with(
                Text.of(BuildGitHubWorkflowRunProperty::name.name)
                    .label("Name")
                    .help("Name of the workflow")
                    .value(value?.name)
            )
            .with(
                Int.of(BuildGitHubWorkflowRunProperty::runNumber.name)
                    .label("Number")
                    .help("Run number")
                    .min(1)
                    .value(value?.runNumber)
            )
            .with(
                YesNo.of(BuildGitHubWorkflowRunProperty::running.name)
                    .label("Running")
                    .help("Is the workflow still running?")
                    .value(value?.running ?: false)
            )
            .textField(BuildGitHubWorkflowRunProperty::event, value?.event)

    override fun fromClient(node: JsonNode): BuildGitHubWorkflowRunProperty = node.parse()

    override fun fromStorage(node: JsonNode): BuildGitHubWorkflowRunProperty = node.parse()

    override fun replaceValue(
        value: BuildGitHubWorkflowRunProperty,
        replacementFunction: Function<String, String>
    ): BuildGitHubWorkflowRunProperty = value

}