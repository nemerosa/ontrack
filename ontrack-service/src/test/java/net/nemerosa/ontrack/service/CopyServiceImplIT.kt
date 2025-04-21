package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.assertPresent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AsAdminTest
class CopyServiceImplIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var service: CopyService

    @Test
    fun cloneProject() {
        val sourceProject = doCreateProject()
        val sourceBranch = doCreateBranch(sourceProject, nd("B1", "Branch B1"))

        asUser().with(sourceProject, ProjectEdit::class.java).execute(Runnable {
            propertyService.editProperty(
                sourceProject,
                TestSimplePropertyType::class.java,
                TestSimpleProperty("http://wiki/P1")
            )
            propertyService.editProperty(
                sourceBranch,
                TestSimplePropertyType::class.java,
                TestSimpleProperty("http://wiki/B1")
            )
        })

        // Request
        val targetProjectName = TestUtils.uid("P")
        val request = ProjectCloneRequest(
            targetProjectName,
            sourceBranch.id,
            listOf(
                Replacement("P1", "P2"),
                Replacement("B1", "B2")
            )
        )

        // Call
        val clonedProject = asAdmin()
            .call { service.cloneProject(sourceProject, request) }

        assertEquals(targetProjectName, clonedProject.name)

        // Checks the branch is created
        val clonedBranch = asUserWithView(clonedProject).call {
            structureService.findBranchByName(clonedProject.name, "B2")
                .orElse(null)
        }
        assertNotNull(clonedBranch, "Cloned branch created")

        // Checks the copy of properties for the project
        var property = asUserWithView(clonedProject).call {
            propertyService.getProperty(
                clonedProject,
                TestSimplePropertyType::class.java
            ).value
        }
        assertNotNull(property)
        assertEquals("http://wiki/P2", property.value)

        // Checks the copy of properties for the branch
        property = asUserWithView(clonedProject).call {
            propertyService.getProperty(
                clonedBranch,
                TestSimplePropertyType::class.java
            ).value
        }
        assertNotNull(property)
        assertEquals("http://wiki/B2", property.value)
    }

    @Test
    fun cloneBranch() {
        val project = doCreateProject()
        val sourceBranch = doCreateBranch(project, nd("B1", "Branch B1"))
        // Request
        val request = BranchCloneRequest(
            "B2",
            listOf(
                Replacement("B1", "B2")
            )
        )

        // Branch properties
        setProperty(sourceBranch, TestSimplePropertyType::class.java, TestSimpleProperty("http://wiki/B1"))

        // Cloning
        val clonedBranch = asUser()
            .with(sourceBranch, ProjectEdit::class.java)
            .call { service.cloneBranch(sourceBranch, request) }

        // Checks the branch is created
        assertNotNull(clonedBranch)

        // Checks the copy of properties for the branch
        val p = getProperty(clonedBranch, TestSimplePropertyType::class.java)
        assertNotNull(p)
        assertEquals("http://wiki/B2", p.value)
    }

    @Test
    @Throws(Exception::class)
    fun bulkUpdateBranch() {
        val branch = doCreateBranch()
        // Request
        val request = BranchBulkUpdateRequest(
            listOf(
                Replacement("B1", "B2")
            )
        )

        // Branch properties
        setProperty(
            branch,
            TestSimplePropertyType::class.java,
            TestSimpleProperty("http://wiki/B1")
        )

        // Updating
        val updatedBranch = asUser().with(
            branch,
            ProjectEdit::class.java
        ).call { service.update(branch, request) }

        // Checks the copy of properties for the branch
        val p = getProperty(updatedBranch, TestSimplePropertyType::class.java)
        assertNotNull(p)
        assertEquals("http://wiki/B2", p.value)
    }

    @Test
    @Throws(Exception::class)
    fun doCopyBranchProperties() {
        val sourceBranch = doCreateBranch()
        val targetBranch = doCreateBranch()
        // Request
        val replacementFn = Replacement.replacementFn(
            listOf(
                Replacement("B1", "B2")
            )
        )

        // Properties for the branch
        setProperty(sourceBranch, TestSimplePropertyType::class.java, TestSimpleProperty("http://wiki/B1"))

        // Copy
        asUser()
            .withView(sourceBranch)
            .with(targetBranch, ProjectEdit::class.java)
            .execute(Runnable { service.copy(targetBranch, sourceBranch, replacementFn, SyncPolicy.COPY) }
            )

        // Checks the copy of properties for the branch
        val p = getProperty(targetBranch, TestSimplePropertyType::class.java)
        assertNotNull(p)
        assertEquals("http://wiki/B2", p.value)
    }

    @Test
    @Throws(Exception::class)
    fun doCopyPromotionLevels() {
        val sourceBranch = doCreateBranch()
        val targetBranch = doCreateBranch()
        // Request
        val replacementFn = Replacement.replacementFn(
            listOf(
                Replacement("P1", "P2")
            )
        )
        // Promotion levels for source
        val sourcePromotionLevel = doCreatePromotionLevel(sourceBranch, nd("copper", "Copper level for P1"))

        // Properties for the promotion level
        setProperty(sourcePromotionLevel, TestSimplePropertyType::class.java, TestSimpleProperty("http://wiki/P1"))

        // Copy
        asUser()
            .withView(sourceBranch)
            .with(targetBranch, ProjectEdit::class.java)
            .execute(Runnable { service.copy(targetBranch, sourceBranch, replacementFn, SyncPolicy.COPY) }
            )

        // Checks the promotion level was created
        val oPL = asUserWithView(targetBranch).call<Optional<PromotionLevel>> {
            structureService.findPromotionLevelByName(
                targetBranch.project.name,
                targetBranch.name,
                "copper"
            )
        }
        assertPresent(oPL) {
            // Checks the copy of properties for the promotion levels
            val p = getProperty(it, TestSimplePropertyType::class.java)
            assertNotNull(p)
            assertEquals("http://wiki/P2", p.value)
        }
    }

    @Test
    fun doCopyValidationStamps() {
        val sourceBranch = doCreateBranch()
        val targetBranch = doCreateBranch()
        // Request
        val replacementFn = Replacement.replacementFn(
            listOf(
                Replacement("P1", "P2")
            )
        )
        // Validation stamps for source
        val sourceValidationStamp = doCreateValidationStamp(sourceBranch, nd("smoke", "Smoke test for P1"))

        // Properties for the validation stamp
        setProperty(
            sourceValidationStamp,
            TestSimplePropertyType::class.java,
            TestSimpleProperty("http://wiki/P1")
        )

        // Copy
        asUser()
            .withView(sourceBranch)
            .withProjectFunction(targetBranch, ProjectEdit::class.java)
            .execute(Runnable { service.copy(targetBranch, sourceBranch, replacementFn, SyncPolicy.COPY) }
            )

        // Checks the validation stamp was created
        val oVS = asUserWithView(targetBranch).call<Optional<ValidationStamp>> {
            structureService.findValidationStampByName(
                targetBranch.project.name,
                targetBranch.name,
                "smoke"
            )
        }
        assertPresent(oVS) {
            // Checks the copy of properties for the validation stamps
            val p = getProperty(it, TestSimplePropertyType::class.java)
            assertNotNull(p)
            assertEquals("http://wiki/P2", p.value)
        }
    }
}