package net.nemerosa.ontrack.extension.av.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import org.apache.commons.codec.digest.DigestUtils

/**
 * Configuration of the auto versioning for one source.
 *
 * @property sourceProject Project to watch
 * @property sourceBranch Branches to watch using a regular expression
 * @property sourcePromotion Promotion to watch
 * @property targetPath Comma-separated list of file to update with the new version
 * @property targetRegex Regex to use in the target file to identify the line to replace
 *                       with the new version. The first matching group must be the version.
 * @property targetProperty Optional replacement for the regex, using only a property name
 * @property targetPropertyRegex Optional regex to use on the [property][targetProperty] value
 * @property targetPropertyType When [targetProperty] is defined, defines the type of property (defaults to Java properties file, but could be NPM, etc.)
 * @property autoApproval Check if the PR must be approved automatically or not (`true` by default)
 * @property upgradeBranchPattern Prefix to use for the upgrade branch in Git, defaults to `feature/auto-upgrade-<project>-<version>`
 * @property postProcessing Type of post processing to launch after the version has been updated
 * @property postProcessingConfig Configuration of the post processing
 * @property validationStamp Validation stamp to create on auto versioning (optional)
 * @property autoApprovalMode Auto approval mode
 */
data class AutoVersioningSourceConfig(
    val sourceProject: String,
    val sourceBranch: String,
    val sourcePromotion: String,
    val targetPath: String,
    override val targetRegex: String?,
    override val targetProperty: String?,
    override val targetPropertyRegex: String?,
    override val targetPropertyType: String?,
    val autoApproval: Boolean?,
    val upgradeBranchPattern: String?,
    val postProcessing: String?,
    val postProcessingConfig: JsonNode?,
    val validationStamp: String?,
    val autoApprovalMode: AutoApprovalMode?,
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
            autoApprovalMode = autoApprovalMode
        )

}