package net.nemerosa.ontrack.extension.av.config

import com.fasterxml.jackson.databind.JsonNode

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
 * @property channel Notification channel
 * @property channelConfig Notification channel configuration
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
    val channel: String?,
    val channelConfig: JsonNode?,
    val autoApprovalMode: AutoApprovalMode?,
) : AutoVersioningTargetConfig {


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

}