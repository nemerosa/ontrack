package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.exceptions.ImageFileSizeException
import net.nemerosa.ontrack.model.exceptions.ImageTypeNotAcceptedException
import net.nemerosa.ontrack.model.security.ProjectList
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Entity.Companion.isEntityDefined
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StructureServiceIT : AbstractDSLTestSupport() {

    @Test(expected = IllegalStateException::class)
    fun `Cannot accept an ID when creating a project`() {
        asAdmin { structureService.newProject(Project.of(nameDescription()).withId(of(1))) }
    }

    @Test
    fun `Creation of a project`() {
        val project = asAdmin { structureService.newProject(Project.of(nameDescription())) }
        isEntityDefined(project, "Project must be defined")
        val p = asUser().withView(project).call { structureService.getProject(project.id) }
        assertEquals(project, p)
    }

    @Test
    fun `Creation of a branch`() {
        val branch = project<Branch> {
            structureService.newBranch(
                    Branch.of(this, nameDescription())
            )
        }
        val b = asUser().withView(branch).call { structureService.getBranch(branch.id) }
        assertEquals(branch, b)
    }

    @Test
    fun `Creation of a promotion level`() {
        val pl = project<PromotionLevel> {
            branch<PromotionLevel> {
                structureService.newPromotionLevel(PromotionLevel.of(this, nameDescription()))
            }
        }
        val p = asUser().withView(pl).call { structureService.getPromotionLevel(pl.id) }
        assertEquals(pl, p)
    }

    @Test
    fun `Creation of a validation stamp`() {
        val vs = project<ValidationStamp> {
            branch<ValidationStamp> {
                structureService.newValidationStamp(ValidationStamp.of(this, nameDescription()))
            }
        }
        val v = asUser().withView(vs).call { structureService.getValidationStamp(vs.id) }
        assertEquals(vs, v)
    }

    @Test
    fun `Creation of a build`() {
        val build = project<Build> {
            branch<Build> {
                structureService.newBuild(Build.of(this, nameDescription(), securityService.currentSignature))
            }
        }
        val b = asUser().withView(build).call { structureService.getBuild(build.id) }
        assertEquals(build, b)
    }

    @Test
    fun `Project list`() {
        val projects = (1..3).map { project() }
        val list = asUserWith<ProjectList, List<Project>> { structureService.projectList }
        assertTrue(list.size >= 3)
        assertTrue(list.containsAll(projects))
    }

    @Test
    fun `Project list filtered by user rights`() {
        withNoGrantViewToAll {
            val projects = (1..3).map { project() }
            val list = asUser().withView(projects[0]).withView(projects[1]).call {
                structureService.projectList
            }
            assertEquals(
                    listOf(projects[0], projects[1]),
                    list
            )
        }
    }

    @Test
    fun `Project list not accessible by default`() {
        withNoGrantViewToAll {
            (1..3).map { project() }
            val list = asUser().call {
                structureService.projectList
            }
            assertTrue(list.isEmpty())
        }
    }

    @Test
    fun `Promotion levels have no image by default`() {
        project {
            branch {
                promotionLevel {
                    val image = structureService.getPromotionLevelImage(id)
                    assertTrue(image.isEmpty, "No image")
                }
            }
        }
    }

    @Test
    fun `Adding an image to a promotion level`() {
        project {
            branch {
                promotionLevel {
                    // Gets an image
                    val image = Document("image/png", TestUtils.resourceBytes("/promotionLevelImage1.png"))
                    // Sets the image
                    structureService.setPromotionLevelImage(id, image)
                    // Gets the image
                    val savedImage = structureService.getPromotionLevelImage(id)
                    assertEquals(image, savedImage)
                }
            }
        }
    }

    @Test
    fun `Non acceptable image type`() {
        project {
            branch {
                promotionLevel {
                    // Gets an image
                    val image = Document("image/x", ByteArray(1))
                    // Sets the image
                    assertFailsWith<ImageTypeNotAcceptedException> {
                        structureService.setPromotionLevelImage(id, image)
                    }
                }
            }
        }
    }

    @Test
    fun `Non acceptable image size`() {
        project {
            branch {
                promotionLevel {
                    // Gets an image
                    val image = Document("image/png", ByteArray(16001))
                    // Sets the image
                    assertFailsWith<ImageFileSizeException> {
                        structureService.setPromotionLevelImage(id, image)
                    }
                }
            }
        }
    }

    @Test
    fun `Removing an image`() {
        project {
            branch {
                promotionLevel {
                    // Gets an image
                    val image = Document("image/png", TestUtils.resourceBytes("/promotionLevelImage1.png"))
                    // Sets the image
                    structureService.setPromotionLevelImage(id, image)
                    // Removes the image
                    structureService.setPromotionLevelImage(id, null)
                    // Gets the image back
                    val savedImage = structureService.getPromotionLevelImage(id)
                    assertTrue(savedImage.isEmpty, "Empty image")
                }
            }
        }
    }

    @Test
    fun `Creation of a validation run must be granted`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    asUser().withView(this).execute {
                        assertFailsWith<AccessDeniedException> {
                            structureService.newValidationRun(
                                    this,
                                    ValidationRunRequest(
                                            vs.name,
                                            ValidationRunStatusID.STATUS_PASSED
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Creation of a validation run`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    asUser().withView(this).with(this, ValidationRunCreate::class.java).execute {
                        structureService.newValidationRun(
                                this,
                                ValidationRunRequest(
                                        vs.name,
                                        ValidationRunStatusID.STATUS_PASSED
                                )
                        )
                    }
                }
            }
        }
    }

}