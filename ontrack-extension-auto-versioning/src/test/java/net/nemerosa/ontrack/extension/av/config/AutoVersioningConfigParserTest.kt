package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.sourceConfig
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AutoVersioningConfigParserTest {

    @Test
    fun `YAML for the simplest configuration`() {
        val source = sourceConfig()
        val config = AutoVersioningConfig(listOf(source))
        val yaml = AutoVersioningConfigParser.toYaml(config)
        assertEquals(
            """
                ---
                configurations:
                - sourceProject: "${source.sourceProject}"
                  sourceBranch: "master"
                  sourcePromotion: "IRON"
                  targetPath: "gradle.properties"
                  targetRegex: null
                  targetProperty: "version"
                  targetPropertyRegex: null
                  targetPropertyType: null
                  autoApproval: null
                  upgradeBranchPattern: null
                  postProcessing: null
                  postProcessingConfig: null
                  validationStamp: null
                  autoApprovalMode: "SCM"
            """.trimIndent().trim(),
            yaml.trim()
        )
    }

    @Test
    fun `YAML for the post processing`() {
        val source = sourceConfig(
            postProcessing = "jenkins",
            postProcessingConfig = mapOf(
                "param" to "value"
            ).asJson(),
        )
        val config = AutoVersioningConfig(listOf(source))
        val yaml = AutoVersioningConfigParser.toYaml(config)
        assertEquals(
            """
                ---
                configurations:
                - sourceProject: "${source.sourceProject}"
                  sourceBranch: "master"
                  sourcePromotion: "IRON"
                  targetPath: "gradle.properties"
                  targetRegex: null
                  targetProperty: "version"
                  targetPropertyRegex: null
                  targetPropertyType: null
                  autoApproval: null
                  upgradeBranchPattern: null
                  postProcessing: "jenkins"
                  postProcessingConfig:
                    param: "value"
                  validationStamp: null
                  autoApprovalMode: "SCM"
            """.trimIndent().trim(),
            yaml.trim()
        )
    }

}