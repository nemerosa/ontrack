package net.nemerosa.ontrack.extension.av.property

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.*
import net.nemerosa.ontrack.model.form.*
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import net.nemerosa.ontrack.model.structure.ServiceConfigurationSource
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class AutoVersioningPropertyType(
    extensionFeature: AutoVersioningExtensionFeature,
) : AbstractPropertyType<AutoVersioningProperty>(extensionFeature) {

    override fun getName(): String = "Auto versioning"

    override fun getDescription(): String = "Setup of the auto versioning"

    override fun getSupportedEntityTypes() = setOf(ProjectEntityType.BRANCH)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
        securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun fromClient(node: JsonNode?): AutoVersioningProperty {
        // Gets the list of configurations
        val property = AutoVersioningProperty(
            configurations = node?.path("configurations")?.map { config ->
                parseClientConfiguration(config)
            } ?: emptyList()
        )
        // Validation
        property.validate()
        // OK
        return property
    }

    private fun parseClientConfiguration(config: JsonNode): AutoVersioningConfig {
        val postProcessingId = config["postProcessing"]?.getTextField("id")
        val postProcessingConfig: JsonNode? = if (postProcessingId != null) {
            parsePostProcessingConfig(config["postProcessing"].get("data"), postProcessingId)
        } else {
            null
        }
        return AutoVersioningConfig(
            sourceProject = config.getRequiredTextField("sourceProject"),
            sourceBranch = config.getRequiredTextField("sourceBranch"),
            sourcePromotion = config.getRequiredTextField("sourcePromotion"),
            targetPath = config.getRequiredTextField("targetPath"),
            targetRegex = config.getTextField("targetRegex"),
            targetProperty = config.getTextField("targetProperty"),
            targetPropertyRegex = config.getTextField("targetPropertyRegex"),
            targetPropertyType = config.getTextField("targetPropertyType"),
            autoApproval = config.getBooleanField("autoApproval"),
            upgradeBranchPattern = config.getTextField("upgradeBranchPattern"),
            postProcessing = postProcessingId,
            postProcessingConfig = postProcessingConfig,
            validationStamp = config.getTextField("validationStamp"),
            channel = config.getTextField("channel"),
            channelConfig = config.getJsonField("channelConfig"),
            autoApprovalMode = config.getEnum<AutoApprovalMode>("autoApprovalMode"),
        )
    }

    private fun parsePostProcessingConfig(jsonNode: JsonNode?, postProcessingId: String?): JsonNode? {
        TODO("Use a post processing registry")
//        return if (postProcessingId != null) {
//            postPromotionService.getPostProcessingById<Any>(postProcessingId)
//                ?.validateJson(jsonNode)
//        } else if (jsonNode != null && !jsonNode.isNull) {
//            throw PostProcessingConfigProvidedWithoutIDException()
//        } else {
//            null
//        }
    }

    override fun fromStorage(node: JsonNode?): AutoVersioningProperty =
        parse(node, AutoVersioningProperty::class.java)

    override fun replaceValue(
        value: AutoVersioningProperty,
        replacementFunction: Function<String, String>,
    ): AutoVersioningProperty = value

    override fun getEditionForm(entity: ProjectEntity?, value: AutoVersioningProperty?): Form =
        TODO()
//        Form.create()
//            .with(
//                MultiForm.of("configurations", createConfigurationForm())
//                    .label("Configurations")
//                    .value(value?.configurations?.map { it.toClient() }
//                        ?: emptyList<ClientAutoVersioningConfig>())
//            )
//
//    private fun createConfigurationForm(): Form {
//        return Form.create()
//            .with(
//                Text.of("sourceProject").label("Source project")
//                    .help("Project to watch")
//            )
//            .with(
//                Text.of("sourceBranch").label("Source branch")
//                    .help("Regular expression to identify the branches to watch for promotion (Git branch).")
//            )
//            .with(
//                Text.of("sourcePromotion").label("Source promotion")
//                    .help("Promotion to watch")
//            )
//            .with(
//                Text.of("targetPath").label("Target path")
//                    .help("""Path to the file to update with the new
//                                    |version, relative to the working copy.""".trimMargin())
//            )
//            .with(
//                Text.of("targetRegex").label("Target regex")
//                    .optional()
//                    .help("""Regex to use in the target file to
//                                    |identify the line to replace with the new version.
//                                    |The first matching group must be the version.""".trimMargin())
//            )
//            .with(
//                Text.of("targetProperty").label("Target property")
//                    .optional()
//                    .help("""... or use only a property name.""")
//            )
//            .with(
//                Text.of("targetPropertyRegex").label("Target property regex")
//                    .optional()
//                    .help("""... using a regular expression to identify a version inside the actual property value""")
//            )
//            .with(
//                Text.of("targetPropertyType").label("Target property type")
//                    .optional()
//                    .help("""... with a given type.""")
//            )
//            .with(
//                YesNo.of("autoApproval").label("Auto approval")
//                    .optional()
//                    .help("""Check if the PR must be approved automatically or not (true by default)""")
//            )
//            .with(
//                Text.of("upgradeBranchPattern").label("Upgrade branch pattern")
//                    .optional()
//                    .help("""Prefix to use for the upgrade branch in Git, defaults to
//                                    |`feature/auto-upgrade-<project>-<version>`. The value must contains `<version>`, which will
//                                    |be replaced by the actual version being upgraded to, and optionally `<project>`, which will
//                                    |be replaced by the source project.""".trimMargin())
//            )
//            .with(
//                ServiceConfigurator.of("postProcessing")
//                    .label("Post processing")
//                    .optional()
//                    .help("Post processing to perform after the upgrade of the version")
//                    .sources(
//                        postPromotionService.allPostProcessings.map { postPromotion ->
//                            ServiceConfigurationSource(
//                                postPromotion.id,
//                                postPromotion.name,
//                                postPromotion.getConfigForm(null)
//                            )
//                        }
//                    )
//            )
//            .with(
//                Text.of("validationStamp").label("Validation stamp to set")
//                    .optional()
//                    .help("""Name of a validation stamp on the target branch or `auto` to create a name automatically based on the source project.""")
//            )
//            .with(
//                Text.of("slackChannel").label("Slack channel")
//                    .optional()
//                    .help("""The slack channel where feedback will be sent whenever any step in the auto-versioning process fails.""")
//            )
//            .with(
//                selection(PRCreationOnPromotion::autoApprovalMode.name, AutoApprovalMode::displayName)
//                    .optional()
//                    .label("Auto approval mode")
//                    .help("Defines the way the PR is merged when auto approval is set. If no value is set, the default settings will be used.")
//            )
//    }

    @Suppress("unused")
    class ClientAutoVersioningConfig(
        val sourceProject: String,
        val sourceBranch: String,
        val sourcePromotion: String,
        val targetPath: String,
        val targetRegex: String?,
        val targetProperty: String?,
        val autoApproval: Boolean?,
        val upgradeBranchPattern: String?,
        val postProcessing: ServiceConfiguration?,
        val validationStamp: String?,
        val channel: String?,
        val channelConfig: JsonNode?,
        val autoApprovalMode: AutoApprovalMode?,
    )
}