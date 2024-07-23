package net.nemerosa.ontrack.extension.av

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractAutoVersioningTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var autoVersioningConfigurationService: AutoVersioningConfigurationService

    @Autowired
    protected lateinit var mockSCMTester: MockSCMTester

    protected fun Branch.setAutoVersioning(
        init: AutoVersioningSetup.() -> Unit,
    ) {
        autoVersioningConfigurationService.setAutoVersioning(this, init)
    }

    protected fun withPromotionLevelTargets(
        code: (
            pl: PromotionLevel,
            app1: Branch,
            app2: Branch,
        ) -> Unit,
    ) {
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

                    code(pl, app1, app2)
                }
            }
        }
    }

    protected fun withSimpleSetup(
        code: (
            pl: PromotionLevel,
            target: Branch,
        ) -> Unit,
    ) {
        asAdmin {
            project {
                val sourceProject = this
                branch {
                    val sourceBranch = this
                    val pl = promotionLevel()

                    val target =
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
                                                    targetPath = "target.properties",
                                                )
                                            )
                                        )
                                    )
                                }
                            }
                        }

                    code(pl, target)
                }
            }
        }
    }

}