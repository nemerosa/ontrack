package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Branch
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class AutoVersioningTargetGQLPromotionLevelFieldContributorIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Test
    fun `Getting the auto-versioning targets for a promotion level`() {
        asAdmin {
            project {
                val sourceProject = this
                branch {
                    val sourceBranch = this
                    val pl = promotionLevel()

                    val app1 =
                        mockSCMTester.withMockSCMRepository {
                            project<Branch> {
                                branch {
                                    configureMockSCMBranch()
                                    autoVersioningConfigurationService.setupAutoVersioning(
                                        this,
                                        AutoVersioningConfig(
                                            configurations = listOf(
                                                AutoVersioningTestFixtures.sourceConfig(
                                                    sourceProject = sourceProject.name,
                                                    sourceBranch = sourceBranch.name,
                                                    sourcePromotion = pl.name,
                                                    targetPath = "app1.properties",
                                                )
                                            )
                                        )
                                    )
                                }
                            }
                        }

                    val app2 =
                        mockSCMTester.withMockSCMRepository {
                            project<Branch> {
                                branch {
                                    configureMockSCMBranch()
                                    autoVersioningConfigurationService.setupAutoVersioning(
                                        this,
                                        AutoVersioningConfig(
                                            configurations = listOf(
                                                AutoVersioningTestFixtures.sourceConfig(
                                                    sourceProject = sourceProject.name,
                                                    sourceBranch = sourceBranch.name,
                                                    sourcePromotion = pl.name,
                                                    targetPath = "app2.properties",
                                                )
                                            )
                                        )
                                    )
                                }
                            }
                        }

                    run(
                        """
                        {
                            promotionLevel(id: ${pl.id}) {
                                autoVersioningTargets {
                                    branch {
                                        id
                                    }
                                    configurations {
                                        targetPath
                                    }
                                }
                            }
                        }
                    """, mapOf(

                        )
                    ) { data ->
                        assertEquals(
                            mapOf(
                                "promotionLevel" to mapOf(
                                    "autoVersioningTargets" to listOf(
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app2.id(),
                                            ),
                                            "configurations" to listOf(
                                                mapOf(
                                                    "targetPath" to "app2.properties",
                                                )
                                            )
                                        ),
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app1.id(),
                                            ),
                                            "configurations" to listOf(
                                                mapOf(
                                                    "targetPath" to "app1.properties",
                                                )
                                            )
                                        ),
                                    )
                                )
                            ).asJson(),
                            data
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Getting the auto-versioning targets for a promotion level with a target being disabled`() {
        asAdmin {
            project {
                val sourceProject = this
                branch {
                    val sourceBranch = this
                    val pl = promotionLevel()

                    val app1 =
                        mockSCMTester.withMockSCMRepository {
                            project<Branch> {
                                branch {
                                    configureMockSCMBranch()
                                    autoVersioningConfigurationService.setupAutoVersioning(
                                        this,
                                        AutoVersioningConfig(
                                            configurations = listOf(
                                                AutoVersioningTestFixtures.sourceConfig(
                                                    sourceProject = sourceProject.name,
                                                    sourceBranch = sourceBranch.name,
                                                    sourcePromotion = pl.name,
                                                    targetPath = "app1.properties",
                                                )
                                            )
                                        )
                                    )
                                }
                            }
                        }

                    mockSCMTester.withMockSCMRepository {
                        project<Branch> {
                            branch {
                                configureMockSCMBranch()
                                autoVersioningConfigurationService.setupAutoVersioning(
                                    this,
                                    AutoVersioningConfig(
                                        configurations = listOf(
                                            AutoVersioningTestFixtures.sourceConfig(
                                                sourceProject = sourceProject.name,
                                                sourceBranch = sourceBranch.name,
                                                sourcePromotion = pl.name,
                                                targetPath = "app2.properties",
                                            )
                                        )
                                    )
                                )
                                structureService.disableBranch(this)
                            }
                        }
                    }

                    run(
                        """
                        {
                            promotionLevel(id: ${pl.id}) {
                                autoVersioningTargets {
                                    branch {
                                        id
                                    }
                                    configurations {
                                        targetPath
                                    }
                                }
                            }
                        }
                    """, mapOf(

                        )
                    ) { data ->
                        assertEquals(
                            mapOf(
                                "promotionLevel" to mapOf(
                                    "autoVersioningTargets" to listOf(
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app1.id(),
                                            ),
                                            "configurations" to listOf(
                                                mapOf(
                                                    "targetPath" to "app1.properties",
                                                )
                                            )
                                        ),
                                    )
                                )
                            ).asJson(),
                            data
                        )
                    }
                }
            }
        }
    }

}