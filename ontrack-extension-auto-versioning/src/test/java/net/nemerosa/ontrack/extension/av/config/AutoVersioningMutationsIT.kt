package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.sourceConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AutoVersioningMutationsIT : AbstractAutoVersioningTestSupport() {

    @Test
    fun `Setting a configuration`() {
        asAdmin {
            project {
                branch {
                    run(
                        """
                        mutation {
                            setAutoVersioningConfig(input: {
                                branchId: $id,
                                configurations: [
                                    {
                                        sourceProject: "A",
                                        sourceBranch: "main",
                                        sourcePromotion: "GOLD",
                                        targetPath: "gradle.properties",
                                        targetProperty: "a-version",
                                    },
                                    {
                                        sourceProject: "B",
                                        sourceBranch: "main",
                                        sourcePromotion: "GOLD",
                                        targetPath: "gradle.properties",
                                        targetProperty: "b-version",
                                        autoApprovalMode: CLIENT,
                                    },
                                ]
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
                    ) { data ->
                        checkGraphQLUserErrors(data, "setAutoVersioningConfig")
                        val config = autoVersioningConfigurationService.getAutoVersioning(this)
                        assertEquals(
                            AutoVersioningConfig(
                                listOf(
                                    sourceConfig(
                                        sourceProject = "A",
                                        sourceBranch = "main",
                                        sourcePromotion = "GOLD",
                                        targetPath = "gradle.properties",
                                        targetProperty = "a-version",
                                    ),
                                    sourceConfig(
                                        sourceProject = "B",
                                        sourceBranch = "main",
                                        sourcePromotion = "GOLD",
                                        targetPath = "gradle.properties",
                                        targetProperty = "b-version",
                                        autoApprovalMode = AutoApprovalMode.CLIENT,
                                    ),
                                )
                            ),
                            config
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Setting a configuration by name`() {
        asAdmin {
            project {
                branch {
                    run(
                        """
                        mutation {
                            setAutoVersioningConfigByName(input: {
                                project: "${project.name}",
                                branch: "$name",
                                configurations: [
                                    {
                                        sourceProject: "A",
                                        sourceBranch: "main",
                                        sourcePromotion: "GOLD",
                                        targetPath: "gradle.properties",
                                        targetProperty: "a-version",
                                    },
                                    {
                                        sourceProject: "B",
                                        sourceBranch: "main",
                                        sourcePromotion: "GOLD",
                                        targetPath: "gradle.properties",
                                        targetProperty: "b-version",
                                        autoApprovalMode: CLIENT,
                                    },
                                ]
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
                    ) { data ->
                        checkGraphQLUserErrors(data, "setAutoVersioningConfigByName")
                        val config = autoVersioningConfigurationService.getAutoVersioning(this)
                        assertEquals(
                            AutoVersioningConfig(
                                listOf(
                                    sourceConfig(
                                        sourceProject = "A",
                                        sourceBranch = "main",
                                        sourcePromotion = "GOLD",
                                        targetPath = "gradle.properties",
                                        targetProperty = "a-version",
                                    ),
                                    sourceConfig(
                                        sourceProject = "B",
                                        sourceBranch = "main",
                                        sourcePromotion = "GOLD",
                                        targetPath = "gradle.properties",
                                        targetProperty = "b-version",
                                        autoApprovalMode = AutoApprovalMode.CLIENT,
                                    ),
                                )
                            ),
                            config
                        )
                    }
                }
            }
        }
    }

}