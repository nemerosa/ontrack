package net.nemerosa.ontrack.extension.github.workflow

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.form.*
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
            .multiform(
                BuildGitHubWorkflowRunProperty::workflows,
                value?.workflows
            ) {
                Form.create()
                    .longField(BuildGitHubWorkflowRun::runId, null)
                    .urlField(BuildGitHubWorkflowRun::url, null)
                    .textField(BuildGitHubWorkflowRun::name, null)
                    .intField(BuildGitHubWorkflowRun::runNumber, null)
                    .yesNoField(BuildGitHubWorkflowRun::running, null)
                    .textField(BuildGitHubWorkflowRun::event, null)
            }

    override fun fromClient(node: JsonNode): BuildGitHubWorkflowRunProperty = node.parse()

    override fun fromStorage(node: JsonNode): BuildGitHubWorkflowRunProperty = node.parse()

    override fun replaceValue(
        value: BuildGitHubWorkflowRunProperty,
        replacementFunction: Function<String, String>
    ): BuildGitHubWorkflowRunProperty = value

}