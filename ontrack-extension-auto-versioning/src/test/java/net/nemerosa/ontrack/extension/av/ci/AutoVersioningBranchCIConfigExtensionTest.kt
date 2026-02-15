package net.nemerosa.ontrack.extension.av.ci

import io.mockk.mockk
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoVersioningBranchCIConfigExtensionTest {

    @Test
    fun `Merging partial configurations with no branch filter`() {
        val extension = AutoVersioningBranchCIConfigExtension(
            autoVersioningExtensionFeature = mockk<AutoVersioningExtensionFeature>(relaxed = true),
            autoVersioningConfigurationService = mockk(),
            branchDisplayNameService = mockk(),
        )
        val config = extension.mergeConfig(
            defaults = AutoVersioningBranchCIConfig(
                configurations = listOf(
                    AutoVersioningSourceConfig(
                        sourceProject = "my-project",
                        sourceBranch = "main",
                        sourcePromotion = "GOLD",
                        targetPath = "versions.properties",
                        targetProperty = "yontrackVersion",
                        validationStamp = "my-chart-validator",
                        disabled = true
                    )
                )
            ),
            custom = mapOf(
                "configurations" to listOf(
                    mapOf(
                        "sourceProject" to "my-project",
                        "sourceBranch" to "main",
                        "sourcePromotion" to "GOLD",
                        "disabled" to false,
                    )
                )
            ).asJson()
        )
        assertEquals(false, config.configurations.single().disabled, "Disabled has been overridden")
    }

    @Test
    fun `Merging branch filters, branch filters are always the default`() {
        val extension = AutoVersioningBranchCIConfigExtension(
            autoVersioningExtensionFeature = mockk<AutoVersioningExtensionFeature>(relaxed = true),
            autoVersioningConfigurationService = mockk(),
            branchDisplayNameService = mockk(),
        )
        val avConfig = AutoVersioningSourceConfig(
            sourceProject = "my-project",
            sourceBranch = "main",
            sourcePromotion = "GOLD",
            targetPath = "versions.properties",
            targetProperty = "yontrackVersion",
            validationStamp = "my-chart-validator",
            disabled = true
        )
        val config = extension.mergeConfig(
            defaults = AutoVersioningBranchCIConfig(
                branchFilter = AutoVersioningBranchCIConfigBranchFilter(
                    includes = listOf("main", "release\\/\\.*"),
                    excludes = listOf("release\\/1\\.*"),
                ),
                configurations = listOf(avConfig),
            ),
            custom = mapOf(
                "branchFilter" to mapOf(
                    "includes" to listOf("main", "release\\/\\.*"),
                    "excludes" to listOf("release\\/0\\.*"),
                ),
            ).asJson()
        )
        assertEquals(
            AutoVersioningBranchCIConfigBranchFilter(
                includes = listOf("main", "release\\/\\.*"),
                excludes = listOf("release\\/1\\.*"),
            ),
            config.branchFilter,
        )
    }

    @Test
    fun `Merging configurations and branch filters, branch filters are always the default`() {
        val extension = AutoVersioningBranchCIConfigExtension(
            autoVersioningExtensionFeature = mockk<AutoVersioningExtensionFeature>(relaxed = true),
            autoVersioningConfigurationService = mockk(),
            branchDisplayNameService = mockk(),
        )
        val config = extension.mergeConfig(
            defaults = AutoVersioningBranchCIConfig(
                branchFilter = AutoVersioningBranchCIConfigBranchFilter(
                    includes = listOf("main", "release\\/\\.*"),
                    excludes = listOf("release\\/1\\.*"),
                ),
                configurations = listOf(
                    AutoVersioningSourceConfig(
                        sourceProject = "my-project",
                        sourceBranch = "main",
                        sourcePromotion = "GOLD",
                        targetPath = "versions.properties",
                        targetProperty = "yontrackVersion",
                        validationStamp = "my-chart-validator",
                        disabled = true
                    )
                )
            ),
            custom = mapOf(
                "branchFilter" to mapOf(
                    "includes" to listOf("main", "release\\/\\.*"),
                    "excludes" to listOf("release\\/0\\.*"),
                ),
                "configurations" to listOf(
                    mapOf(
                        "sourceProject" to "my-project",
                        "sourceBranch" to "main",
                        "sourcePromotion" to "GOLD",
                        "disabled" to false,
                    )
                )
            ).asJson()
        )
        assertEquals(false, config.configurations.single().disabled, "Disabled has been overridden")
        assertEquals(
            AutoVersioningBranchCIConfigBranchFilter(
                includes = listOf("main", "release\\/\\.*"),
                excludes = listOf("release\\/1\\.*"),
            ),
            config.branchFilter,
        )
    }

}