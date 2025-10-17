package net.nemerosa.ontrack.extension.config.graphql

import net.nemerosa.ontrack.extension.config.ConfigTestSupport
import net.nemerosa.ontrack.extension.config.model.*
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLRootQueryCIConfigurationIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var configTestSupport: ConfigTestSupport

    @Test
    @AsAdminTest
    fun `Getting the default effective configuration`() {
        configTestSupport.graphQLEffectiveConfiguration(
            ci = "generic",
            scm = "mock",
        ) { config ->
            assertEquals(
                EffectiveConfiguration(
                    configuration = Configuration(),
                    ciEngine = "generic",
                    scmEngine = "mock",
                ),
                config
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Getting the effective configuration with some matching conditions`() {
        configTestSupport.graphQLEffectiveConfiguration(
            yaml = """
                version: v1
                configuration:
                  defaults:
                      branch:
                        validations:
                          build:
                            tests: {}
                  custom:
                    configs:
                      - conditions:
                          branch: release.*
                        branch:
                          validations:
                            deploy-tests:
                              tests:
                                warningIfSkipped: true
            """.trimIndent(),
            ci = "generic",
            scm = "mock",
            env = mapOf("BRANCH_NAME" to "release/1.0"),
        ) { config ->
            assertEquals(
                EffectiveConfiguration(
                    configuration = Configuration(
                        branch = BranchConfiguration(
                            validations = listOf(
                                ValidationStampConfiguration(
                                    name = "build",
                                    validationStampDataConfiguration = ValidationStampDataConfiguration(
                                        type = "net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType",
                                        data = mapOf(
                                            "warningIfSkipped" to false,
                                            "failWhenNoResults" to false,
                                        ).asJson(),
                                    )
                                ),
                                ValidationStampConfiguration(
                                    name = "deploy-tests",
                                    validationStampDataConfiguration = ValidationStampDataConfiguration(
                                        type = "net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType",
                                        data = mapOf(
                                            "warningIfSkipped" to true,
                                            "failWhenNoResults" to false,
                                        ).asJson(),
                                    )
                                ),
                            )
                        ),
                    ),
                    ciEngine = "generic",
                    scmEngine = "mock",
                ),
                config
            )
        }
    }

}