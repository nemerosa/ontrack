package net.nemerosa.ontrack.extension.av.config

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AutoVersioningSourceConfigTest {

    @Test
    fun `Target paths when only one path`() {
        assertEquals(
            listOf("Jenkinsfile"),
            createConfig(targetPath = "Jenkinsfile").targetPaths
        )
    }

    @Test
    fun `Target paths when several paths`() {
        assertEquals(
            listOf(
                "Jenkinsfile",
                "Jenkinsfile.acceptance",
                "Jenkinsfile.nightly",
                ".jobs/pipelines/deployDGCDocker.groovy"
            ),
            createConfig(targetPath = "Jenkinsfile, Jenkinsfile.acceptance, Jenkinsfile.nightly, .jobs/pipelines/deployDGCDocker.groovy").targetPaths
        )
    }

    @Test
    fun `Missing both target regex and target property`() {
        assertFailsWith<MissingTargetRegexOrPropertyException> {
            createConfig(targetRegex = null, targetProperty = null).validate()
        }
        assertFailsWith<MissingTargetRegexOrPropertyException> {
            createConfig(targetRegex = "", targetProperty = null).validate()
        }
        assertFailsWith<MissingTargetRegexOrPropertyException> {
            createConfig(targetRegex = "", targetProperty = "").validate()
        }
        assertFailsWith<MissingTargetRegexOrPropertyException> {
            createConfig(targetRegex = null, targetProperty = "").validate()
        }
    }

    @Test
    fun `At least target regex or target property`() {
        createConfig(targetRegex = "my-version = (.*)", targetProperty = null).validate()
        createConfig(targetRegex = "my-version = (.*)", targetProperty = "my-version").validate()
        createConfig(targetRegex = null, targetProperty = "my-version").validate()
    }

    @Test
    fun `Null upgrade branch pattern is valid`() {
        val config = createConfig(upgradeBranchPattern = null)
        config.validate()
    }

    @Test
    fun `Upgrade branch pattern with version and project is valid`() {
        val config = createConfig(
            upgradeBranchPattern = "my-custom-branch-with-<project>-and-<version>"
        )
        config.validate()
    }

    @Test
    fun `Upgrade branch pattern with version and no project is valid`() {
        val config = createConfig(
            upgradeBranchPattern = "my-custom-branch-with-<version>"
        )
        config.validate()
    }

    @Test
    fun `Upgrade branch pattern with no version is not valid`() {
        val config = createConfig(
            upgradeBranchPattern = "my-custom-branch-with-<project>"
        )
        assertFailsWith<UpgradeBranchPrefixNoVersionException> {
            config.validate()
        }
    }

    @Test
    fun `Default upgrade branch with hash`() {
        assertEquals(
            "feature/auto-upgrade-PRJ-1.0.0-282ac72ea57724cbae42b590bc0bf3a0",
            AutoVersioningSourceConfig.getUpgradeBranch(
                AutoVersioningSourceConfig.DEFAULT_UPGRADE_BRANCH_PATTERN,
                "PRJ",
                "1.0.0",
                "branch-name",
                true
            )
        )
    }

    @Test
    fun `Default upgrade branch without hash`() {
        assertEquals(
            "feature/auto-upgrade-PRJ-1.0.0-branch-name",
            AutoVersioningSourceConfig.getUpgradeBranch(
                AutoVersioningSourceConfig.DEFAULT_UPGRADE_BRANCH_PATTERN,
                "PRJ",
                "1.0.0",
                "branch-name",
                false
            )
        )
    }

    @Test
    fun `Branch with project and version`() {
        assertEquals(
            "custom-with-PRJ-and-1.0.0",
            AutoVersioningSourceConfig.getUpgradeBranch(
                "custom-with-<project>-and-<version>",
                "PRJ",
                "1.0.0",
                "any",
                true
            )
        )
    }

    @Test
    fun `Branch with version only`() {
        assertEquals(
            "custom-for-1.0.0",
            AutoVersioningSourceConfig.getUpgradeBranch(
                "custom-for-<version>",
                "PRJ",
                "1.0.0",
                "any",
                true
            )
        )
    }

    private fun createConfig(
        targetRegex: String? = null,
        targetPath: String = "gradle.properties",
        targetProperty: String? = "version",
        upgradeBranchPattern: String? = null,
    ) =
        AutoVersioningSourceConfig(
            sourceProject = "PRJ",
            sourceBranch = "master",
            sourcePromotion = "IRON",
            targetPath = targetPath,
            targetRegex = targetRegex,
            targetProperty = targetProperty,
            targetPropertyRegex = null,
            targetPropertyType = null,
            autoApproval = null,
            upgradeBranchPattern = upgradeBranchPattern,
            postProcessing = null,
            postProcessingConfig = null,
            validationStamp = null,
            autoApprovalMode = AutoApprovalMode.SCM
        )

}
