package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.PromotionLevelCreate
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * This class supersedes [StructureServiceIT], which cannot be replaced as yet.
 */
class StructureServiceNewIT : AbstractDSLTestSupport() {

    @Test
    fun `Looking for projects using a pattern`() {
        val rootA = uid("P")
        val rootB = uid("P")
        repeat(5) {
            project(name = NameDescription.nd("X${rootA}$it", ""))
        }
        repeat(5) {
            project(name = NameDescription.nd("Y${rootB}$it", ""))
        }
        asAdmin {
            val projects = structureService.findProjectsByNamePattern(rootA)
            assertEquals(
                (0..4).map { "X$rootA$it"},
                projects.map { it.name }
            )
        }
    }

    @Test
    fun `Looking for projects using a pattern is restricted by authorizations`() {
        val rootA = uid("P")
        val rootB = uid("P")
        val projectsA = (0..4).map {
            project(name = NameDescription.nd("X${rootA}$it", ""))
        }
        repeat(5) {
            project(name = NameDescription.nd("Y${rootB}$it", ""))
        }
        withNoGrantViewToAll {
            asUserWithView(*projectsA.take(3).toTypedArray()) {
                val projects = structureService.findProjectsByNamePattern(rootA)
                assertEquals(
                    (0..2).map { "X$rootA$it"},
                    projects.map { it.name }
                )
            }
        }
    }

    @Test
    fun `An admin can change the signature of a branch`() {
        project {
            val branch = branch {
                updateBranchSignature(time = Time.now().minusDays(10))
            }
            // Loads the branch again
            asUserWithView {
                val reloaded = structureService.getBranch(branch.id)
                assertEquals(10, Duration.between(reloaded.signature.time, Time.now()).toDays())
            }
        }
    }

    @Test
    fun `Validation run status comment not editable by default`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    validate(vs, ValidationRunStatusID.STATUS_FAILED)
                    // Second validation with comment
                    val run = asUser().with(this, ValidationRunStatusChange::class.java).call {
                        validate(vs, ValidationRunStatusID.STATUS_INVESTIGATING, "First comment")
                    }
                    val statusId = run.lastStatus.id
                    // Not editable by anonymous user
                    asAnonymous().execute {
                        val editable = structureService.isValidationRunStatusCommentEditable(statusId)
                        assertFalse(editable, "Status comment by default not editable")
                    }
                }
            }
        }
    }

    @Test
    fun `New promotion level based on a predefined promotion level with description and image`() {
        // Unique promotion name
        val promotionName = uid("P")
        // Predefined promotion level
        predefinedPromotionLevel(promotionName, "My predefined description", image = true)
        // Creating a promotion level
        project {
            branch {
                asUser().with(this, PromotionLevelCreate::class.java).call {
                    val pl = structureService.newPromotionLevel(
                            PromotionLevel.of(
                                    this,
                                    NameDescription.nd(promotionName, "")
                            )
                    )
                    // Description must be aligned
                    assertEquals("My predefined description", pl.description)
                    // An image must have been set
                    assertTrue(pl.isImage, "An image must have been set")
                }
            }
        }
    }

    @Test
    fun `New validation stamp based on a predefined validation stamp with description and image`() {
        // Unique validation name
        val validationName = uid("V")
        // Predefined validation stamp
        predefinedValidationStamp(validationName, "My predefined description", image = true)
        // Creating a validation stamp
        project {
            branch {
                asUser().with(this, ValidationStampCreate::class.java).call {
                    val vs = structureService.newValidationStamp(
                            ValidationStamp.of(
                                    this,
                                    NameDescription.nd(validationName, "")
                            )
                    )
                    // Description must be aligned
                    assertEquals("My predefined description", vs.description)
                    // An image must have been set
                    assertTrue(vs.isImage, "An image must have been set")
                }
            }
        }
    }

}