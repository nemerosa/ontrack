package net.nemerosa.ontrack.extension.av.ci

import io.mockk.mockk
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AutoVersioningBranchCIConfigExtensionTest {

    @Test
    fun `Merging partial configurations`() {
        val extension = AutoVersioningBranchCIConfigExtension(
            autoVersioningExtensionFeature = mockk<AutoVersioningExtensionFeature>(relaxed = true),
            autoVersioningConfigurationService = mockk(),
        )
        val config = extension.mergeConfig(
            defaults = AutoVersioningConfig(
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

}