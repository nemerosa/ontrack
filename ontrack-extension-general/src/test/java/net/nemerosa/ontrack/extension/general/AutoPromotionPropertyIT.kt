package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.form.MultiSelection
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.BranchCloneRequest
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.CopyService
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AutoPromotionPropertyIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var copyService: CopyService

    @Autowired
    private lateinit var autoPromotionPropertyType: AutoPromotionPropertyType

    /**
     * #850 - When the auto promotion is empty, do not promote automatically
     */
    @Test
    fun `Empty auto promotion property does not trigger promotion`() {
        project {
            branch {
                val vs = validationStamp()
                val pl = promotionLevel {
                    setProperty(
                        this,
                        AutoPromotionPropertyType::class.java,
                        AutoPromotionProperty(emptyList(), "", "", emptyList())
                    )
                }
                build {
                    // Validation
                    validate(vs)
                    // Checks that the build has not been promoted
                    val runs = structureService.getPromotionRunsForBuildAndPromotionLevel(this, pl)
                    assertTrue(runs.isEmpty(), "Build was not promoted")
                }
            }
        }
    }

    /**
     * This test checks that a promotion level is not attributed twice on validation upon auto promotion.
     */
    @Test
    fun `Auto promotion - only once`() {
        // Creation of a branch
        project {
            branch {
                // Creation of validation stamps
                val vs1 = validationStamp("CI.1")
                val vs2 = validationStamp("CI.2")
                // Creation of one promotion level
                val promotionLevel = promotionLevel("PL")
                // Sets the auto promotion
                setProperty(
                        promotionLevel,
                        AutoPromotionPropertyType::class.java,
                        AutoPromotionProperty(emptyList(), "CI.*", "", emptyList())
                )
                // Creates a build
                build("1") {
                    // Validates the build once
                    validate(vs1)
                    validate(vs2)
                    // Checks the promotion of the build
                    asUser().withView(branch).call {
                        val runs = structureService.getPromotionRunsForBuild(id)
                        assertEquals(
                                listOf("PL"),
                                runs.map { it.promotionLevel.name }
                        )
                    }
                    // Validates the build a second time
                    validate(vs1)
                    // Checks the promotion of the build (only one)
                    asUser().withView(branch).call {
                        val runs = structureService.getPromotionRunsForBuild(id)
                        assertEquals(
                                listOf("PL"),
                                runs.map { it.promotionLevel.name }
                        )
                    }
                }
            }
        }
    }

    /**
     * This test checks that whenever a validation stamp, which was part of an auto promotion configuration,
     * is deleted, it is automatically removed from the auto promotion configuration.
     */
    @Test
    fun `Auto promotion - auto configuration on validation stamp deletion`() {
        // Creation of a branch
        project {
            branch {
                // Creation of two validation stamps
                val vs1 = validationStamp("VS1")
                val vs2 = validationStamp("VS2")
                // Creation of one promotion level
                val promotionLevel = promotionLevel("PL")
                // Sets the auto promotion
                setProperty(
                        promotionLevel,
                        AutoPromotionPropertyType::class.java,
                        AutoPromotionProperty(listOf(vs1, vs2), "", "", emptyList())
                )
                // Deletes a validation stamp
                asUser().with(this, ProjectEdit::class.java).with(this, ProjectEdit::class.java).call {
                    structureService.deleteValidationStamp(vs1.id)
                }
                // Gets the auto promotion configuration
                val property: AutoPromotionProperty? = getProperty(promotionLevel, AutoPromotionPropertyType::class.java)
                // Checks it does not contain the VS1 any longer
                assertNotNull(property) {
                    assertEquals(
                            listOf("VS2"),
                            it.validationStamps.map { it.name }
                    )
                }
            }
        }
    }

    /**
     * Regression test for #290
     */
    @Test
    fun `Branch cloning with auto promotion`() {
        // Creates a promotion level
        project {
            branch {
                val promotionLevel = promotionLevel("PL")
                // Creates a validation stamp
                val vs1 = validationStamp("VS1")
                validationStamp("VS2")
                // Auto promotion
                setProperty(
                        promotionLevel,
                        AutoPromotionPropertyType::class.java,
                        AutoPromotionProperty(listOf(vs1), "", "", emptyList())
                )
                // Cloning the branch
                val clonedBranchName = TestUtils.uid("B")
                val clonedBranch = asUser().with(this, ProjectEdit::class.java).call {
                    copyService.cloneBranch(
                            this,
                            BranchCloneRequest(
                                    clonedBranchName,
                                    emptyList()
                            )
                    )
                }
                assertEquals(clonedBranchName, clonedBranch.name)
                // Gets the cloned promotion level
                val clonedPromotionLevel: PromotionLevel? = asUser().withView(clonedBranch).call {
                    structureService.findPromotionLevelByName(
                            clonedBranch.project.name,
                            clonedBranch.name,
                            promotionLevel.name
                    ).orElse(null)
                }

                assertNotNull(clonedPromotionLevel) {
                    // Gets the auto validation property for the cloned branch
                    val property: AutoPromotionProperty? = asUser().withView(it).call {
                        getProperty(it, AutoPromotionPropertyType::class.java)
                    }
                    assertNotNull(property) {
                        assertEquals(
                                listOf("VS1"),
                                it.validationStamps.map { it.name }
                        )
                    }
                }
            }
        }
    }

    /**
     * Backward compatibility test from 3.42 when `promotionLevels` was not set.
     */
    @Test
    fun `Backward compatibility parsing from 3-42`() {
        project {
            branch {
                val vs1 = validationStamp()
                val vs2 = validationStamp()
                val node = mapOf(
                        "validationStamps" to listOf(vs1.id(), vs2.id()),
                        "include" to "INCLUDE.*",
                        "exclude" to "EXCLUDE.*"
                ).asJson()
                val property = autoPromotionPropertyType.fromStorage(node)
                assertEquals(listOf(vs1, vs2), property.validationStamps)
                assertEquals("INCLUDE.*", property.include)
                assertEquals("EXCLUDE.*", property.exclude)
                assertEquals(emptyList(), property.promotionLevels)
            }
        }
    }

    @Test
    fun `Auto promotion based on another promotion`() {
        project {
            branch {
                val silver = promotionLevel()
                val gold = promotionLevel()
                // Auto promotion
                setProperty(
                        gold,
                        AutoPromotionPropertyType::class.java,
                        AutoPromotionProperty(emptyList(), "", "", listOf(silver))
                )
                // Testing on a build
                build {
                    // Promotes to silver
                    promote(silver)
                    // ... checks it has been promoted to gold
                    checkBuildIsPromoted(this, gold)
                }
            }
        }
    }

    @Test
    fun `Auto promotion based on another promotion and validation stamps`() {
        project {
            branch {
                val silver = promotionLevel()
                val gold = promotionLevel()
                val quality = validationStamp()
                val security = validationStamp()
                // Auto promotion
                setProperty(
                        gold,
                        AutoPromotionPropertyType::class.java,
                        AutoPromotionProperty(listOf(quality, security), "", "", listOf(silver))
                )
                // Testing on a build
                build {
                    // Performs validations
                    validate(quality)
                    validate(security)
                    // ... checks it's not promoted to gold yet
                    checkBuildIsNotPromoted(this, gold)
                    // Promotes to silver
                    promote(silver)
                    // ... checks it has been promoted to gold
                    checkBuildIsPromoted(this, gold)
                }
            }
        }
    }

    @Test
    fun `Auto promotion based on another promotion and validation stamps with promotion first`() {
        project {
            branch {
                val silver = promotionLevel()
                val gold = promotionLevel()
                val quality = validationStamp()
                val security = validationStamp()
                // Auto promotion
                setProperty(
                        gold,
                        AutoPromotionPropertyType::class.java,
                        AutoPromotionProperty(listOf(quality, security), "", "", listOf(silver))
                )
                // Testing on a build
                build {
                    // Promotes to silver
                    promote(silver)
                    // ... checks it's not promoted to gold yet
                    checkBuildIsNotPromoted(this, gold)
                    // Performs validations
                    validate(quality)
                    validate(security)
                    // ... checks it has been promoted to gold
                    checkBuildIsPromoted(this, gold)
                }
            }
        }
    }

    @Test
    fun `Auto promotion based on same promotion has no effect`() {
        project {
            branch {
                val gold = promotionLevel()
                // Auto promotion
                setProperty(
                        gold,
                        AutoPromotionPropertyType::class.java,
                        AutoPromotionProperty(emptyList(), "", "", listOf(gold))
                )
                // Testing on a build
                build {
                    // Promotes to gold
                    promote(gold)
                    // ... checks it has been promoted to gold only once
                    asUserWithView {
                        val runs = structureService.getPromotionRunsForBuildAndPromotionLevel(this, gold)
                        assertEquals(1, runs.size)
                    }
                }
            }
        }
    }

    @Test
    fun `Auto promotion adjusted on promotion deletion`() {
        project {
            branch {
                // Creation of two promotion levels
                val iron = promotionLevel()
                val silver = promotionLevel()
                val gold = promotionLevel()
                // Sets the auto promotion
                setProperty(
                        gold,
                        AutoPromotionPropertyType::class.java,
                        AutoPromotionProperty(emptyList(), "", "", listOf(iron, silver))
                )
                // Deletes the promotion level depended upon
                asUser().with(this, ProjectEdit::class.java).with(this, ProjectEdit::class.java).call {
                    structureService.deletePromotionLevel(silver.id)
                }
                // Gets the auto promotion configuration
                val property: AutoPromotionProperty? = getProperty(gold, AutoPromotionPropertyType::class.java)
                // Checks it does not contain the silver promotion any longer
                assertNotNull(property) {
                    assertEquals(
                            listOf(iron.id),
                            it.promotionLevels.map { pl -> pl.id }
                    )
                }
            }
        }
    }

    @Test
    fun `Auto promotion cannot use itself as a dependency`() {
        project {
            branch {
                val iron = promotionLevel()
                val silver = promotionLevel()
                val gold = promotionLevel()
                // Gets the edition form...
                val form = autoPromotionPropertyType.getEditionForm(gold, null)
                // ... and checks that all promotion levels are available but the one being configured
                val field = form.getField("promotionLevels") as MultiSelection
                val promotions = field.items.map { it.id }
                assertEquals(listOf(iron.id().toString(), silver.id().toString()), promotions)
            }
        }
    }

    private fun checkBuildIsPromoted(build: Build, promotionLevel: PromotionLevel) {
        build.asUserWithView {
            val runs = structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel)
            assertEquals(
                    listOf(promotionLevel.id),
                    runs.map { it.promotionLevel.id }
            )
        }
    }

    private fun checkBuildIsNotPromoted(build: Build, promotionLevel: PromotionLevel) {
        build.asUserWithView {
            val runs = structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel)
            assertTrue(runs.isEmpty())
        }
    }
}
