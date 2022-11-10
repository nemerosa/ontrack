package net.nemerosa.ontrack.extension.av.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.apache.commons.codec.digest.DigestUtils

@APIDescription("Configuration of the auto versioning for one source.")
data class AutoVersioningSourceConfig(
    @APIDescription("Project to watch")
    val sourceProject: String,
    @APIDescription("Branches to watch using a regular expression")
    val sourceBranch: String,
    @APIDescription("Promotion to watch")
    val sourcePromotion: String,
    @APIDescription("Comma-separated list of file to update with the new version")
    val targetPath: String,
    @APIDescription("Regex to use in the target file to identify the line to replace with the new version. The first matching group must be the version.")
    override val targetRegex: String? = null,
    @APIDescription("Optional replacement for the regex, using only a property name")
    override val targetProperty: String? = null,
    @APIDescription("Optional regex to use on the targetProperty value")
    override val targetPropertyRegex: String? = null,
    @APIDescription("When targetProperty is defined, defines the type of property (defaults to Java properties file, but could be NPM, etc.)")
    override val targetPropertyType: String? = null,
    @APIDescription("Check if the PR must be approved automatically or not (`true` by default)")
    val autoApproval: Boolean? = null,
    @APIDescription("Prefix to use for the upgrade branch in Git, defaults to `feature/auto-upgrade-<project>-<version>`")
    val upgradeBranchPattern: String? = null,
    @APIDescription("Type of post processing to launch after the version has been updated")
    val postProcessing: String? = null,
    @APIDescription("Configuration of the post processing")
    val postProcessingConfig: JsonNode? = null,
    @APIDescription("Validation stamp to create on auto versioning (optional)")
    val validationStamp: String? = null,
    @APIDescription("Auto approval mode")
    val autoApprovalMode: AutoApprovalMode? = null,
    @APIDescription("Build link creation. True by default.")
    val buildLinkCreation: Boolean? = null,
    @APIDescription("List of notifications subscriptions to setup for this auto versioning")
    @ListRef(embedded = true, suffix = "Input")
    val notifications: List<AutoVersioningNotification>? = null,
) : AutoVersioningTargetConfig {

    /**
     * Gets the list of paths
     */
    @JsonIgnore
    fun getTargetPaths(): List<String> = targetPath.split(",").map { it.trim() }

    /**
     * Validates that this configuration is consistent. Checks that at least the regex or the property is set,
     * and that, if it is defined, the upgrade branch pattern contains the `<version>` token.
     */
    fun validate() {
        validateTargetRegex()
        validateUpgradeBranchPrefix()
    }

    private fun validateTargetRegex() {
        if (targetRegex.isNullOrBlank() && targetProperty.isNullOrBlank()) {
            throw MissingTargetRegexOrPropertyException()
        }
    }

    private fun validateUpgradeBranchPrefix() {
        if (upgradeBranchPattern != null && !upgradeBranchPattern.contains("<version>")) {
            throw UpgradeBranchPrefixNoVersionException(upgradeBranchPattern)
        }
    }

    companion object {

        val DEFAULT_AUTO_APPROVAL_MODE = AutoApprovalMode.CLIENT

        const val DEFAULT_UPGRADE_BRANCH_PATTERN = "feature/auto-upgrade-<project>-<version>-<branch>"

        /**
         * Given a [branch pattern][upgradeBranchPattern], computes an actual branch name
         * by replacing the `<project>`, `<branch>` and `<version>` tokens by their respective values.
         *
         * @param upgradeBranchPattern Branch pattern
         * @param project Replacement of the `<project>` token
         * @param version Replacement of the `<version>` token
         * @param branch Replacement of the `<branch>` token
         * @param branchHash Flag to indicate if the [branch] value must be hashed before being injected (because
         * a complete branch name would be too long)
         * @return Branch name to use
         */
        fun getUpgradeBranch(
            upgradeBranchPattern: String,
            project: String,
            version: String,
            branch: String,
            branchHash: Boolean,
        ): String {
            val branchToken = if (branchHash) {
                DigestUtils.md5Hex(branch)
            } else {
                branch
            }
            return upgradeBranchPattern
                .replace("<project>", project)
                .replace("<version>", version)
                .replace("<branch>", branchToken)
        }

    }

    fun postDeserialize() =
        AutoVersioningSourceConfig(
            sourceProject = sourceProject,
            sourceBranch = sourceBranch,
            sourcePromotion = sourcePromotion,
            targetPath = targetPath,
            targetRegex = targetRegex,
            targetProperty = targetProperty,
            targetPropertyRegex = targetPropertyRegex,
            targetPropertyType = targetPropertyType,
            autoApproval = autoApproval,
            upgradeBranchPattern = upgradeBranchPattern,
            postProcessing = postProcessing,
            postProcessingConfig = postProcessingConfig?.takeIf { !it.isNull },
            validationStamp = validationStamp,
            autoApprovalMode = autoApprovalMode,
            buildLinkCreation = buildLinkCreation,
            notifications = notifications,
        )

}