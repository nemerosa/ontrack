package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.BranchCloneRequest
import net.nemerosa.ontrack.model.structure.CopyService
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AutoPromotionPropertyIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var copyService: CopyService

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
                        AutoPromotionProperty(emptyList(), "CI.*", "")
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
                        AutoPromotionProperty(listOf(vs1, vs2), "", "")
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
                        AutoPromotionProperty(listOf(vs1), "", "")
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
}
