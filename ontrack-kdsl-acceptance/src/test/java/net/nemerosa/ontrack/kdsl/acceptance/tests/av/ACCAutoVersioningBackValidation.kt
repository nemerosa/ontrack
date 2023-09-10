package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.general.AutoPromotionProperty
import net.nemerosa.ontrack.kdsl.spec.extension.general.autoPromotion
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ACCAutoVersioningBackValidation : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Back validation of the source build`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForMockRepository()
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "some-version",
                                    targetPropertyType = "properties",
                                    backValidation = "back-validation-vs",
                                )
                            )
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")

                                waitForAutoVersioningCompletion()

                                assertThatMockScmRepository {
                                    hasPR(
                                        from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
                                        to = "main"
                                    )
                                    fileContains("gradle.properties") {
                                        "some-version = 2.0.0"
                                    }
                                }

                                // Checks that the source build has been validated with the back validation
                                val run = this.getValidationRuns(validationStamp = "back-validation-vs", count = 1)
                                    .firstOrNull()

                                assertNotNull(run, "Source build has been validated") {
                                    assertEquals("PASSED", it.lastStatus.id)
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * If a back validation on auto versioning creates another promotion which
     * triggers again the same validation, which creates the same promotion, etc.
     *
     * This would be a misconfiguration but this could really create havoc in Ontrack.
     */
    @Test
    fun `Preventing recursive calls when using back validation`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }

                // Source branch
                project {
                    branch {
                        val dependency = this
                        val vs = validationStamp(name = "back-validation-vs")
                        val pl = promotion(name = "IRON")

                        // Setting auto promotion on this promotion level, based on the back validation
                        // (this is stupid, but you never know...)

                        pl.autoPromotion = AutoPromotionProperty(
                            validationStamps = listOf(vs.id)
                        )

                        // Target branch
                        project {
                            branch {
                                configuredForMockRepository()
                                setAutoVersioningConfig(
                                    listOf(
                                        AutoVersioningSourceConfig(
                                            sourceProject = dependency.project.name,
                                            sourceBranch = dependency.name,
                                            sourcePromotion = "IRON",
                                            targetPath = "gradle.properties",
                                            targetProperty = "some-version",
                                            targetPropertyType = "properties",
                                            backValidation = "back-validation-vs",
                                        )
                                    )
                                )

                                dependency.apply {
                                    build(name = "2.0.0") {
                                        promote("IRON")

                                        waitForAutoVersioningCompletion()

                                        assertThatMockScmRepository {
                                            hasPR(
                                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
                                                to = "main"
                                            )
                                            fileContains("gradle.properties") {
                                                "some-version = 2.0.0"
                                            }
                                        }

                                        // Checks that the source build has been validated with the back validation
                                        val run = this.getValidationRuns(validationStamp = "back-validation-vs", count = 1)
                                            .firstOrNull()

                                        assertNotNull(run, "Source build has been validated") {
                                            assertEquals("PASSED", it.lastStatus.id)
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

            }
        }
    }

}