package net.nemerosa.ontrack.boot.ui

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.PaginationCountException
import net.nemerosa.ontrack.ui.resource.PaginationOffsetException
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidationRunControllerTest {

    private lateinit var controller: ValidationRunController
    private lateinit var structureService: StructureService
    private lateinit var propertyService: PropertyService
    private lateinit var predefinedValidationStampService: PredefinedValidationStampService
    private lateinit var securityService: SecurityService

    private val branch = Branch.of(
            Project.of(nd("P", "Project")).withId(ID.of(1)),
            nd("B", "Branch")
    ).withId(ID.of(1))

    private val validationStamp = ValidationStamp.of(
            branch,
            nd("VS", "Validation stamp")
    ).withId(ID.of(1))

    @Before
    fun before() {
        structureService = mock()
        val validationRunStatusService = mock<ValidationRunStatusService>()
        val validationDataTypeService = mock<ValidationDataTypeService>()
        propertyService = mock()
        securityService = mock()
        predefinedValidationStampService = mock()
        controller = ValidationRunController(
                structureService,
                validationRunStatusService,
                validationDataTypeService,
                securityService
        )
        // Mock URI builder for tests
        val field = AbstractResourceController::class.java.getDeclaredField("uriBuilder")
        field.isAccessible = true
        field.set(controller, MockURIBuilder())
    }

    private fun generateRuns(count: Int): List<ValidationRun> =
            (1..count).map {
                val build = Build.of(
                        branch,
                        nd("$it", "Build $it"),
                        Signature.of("user")
                ).withId(ID.of(it))
                ValidationRun.of(
                        build,
                        validationStamp,
                        it,
                        Signature.of("user"),
                        ValidationRunStatusID.STATUS_PASSED,
                        ""
                )
            }

    @Test
    fun `getValidationRunsForValidationStamp no result`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(emptyList())
        val resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assertNotNull(resources) {
            assertTrue(it.resources.isEmpty())
            assertNotNull(it.pagination) { p ->
                assertNull(p.prev)
                assertNull(p.next)
                assertEquals(0, p.offset)
                assertEquals(10, p.limit)
                assertEquals(0, p.total)
            }
        }
    }

    @Test
    fun `getValidationRunsForValidationStamp first page`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(generateRuns(30))
        val resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assertNotNull(resources) {
            assertEquals(10, it.resources.size)
            assertNotNull(it.pagination) { p ->
                assertNull(p.prev)
                assertEquals(p.next.toString(), "urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRunsForValidationStamp:1,10,10")
                assertEquals(0, p.offset)
                assertEquals(10, p.limit)
                assertEquals(30, p.total)
            }
        }
    }

    @Test
    fun `getValidationRunsForValidationStamp middle page`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(generateRuns(30))
        val resources = controller.getValidationRunsForValidationStamp(ID.of(1), 10, 10)
        assertNotNull(resources) {
            assertEquals(10, it.resources.size)
            assertNotNull(it.pagination) { p ->
                assertEquals(p.prev.toString(), "urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRunsForValidationStamp:1,0,10")
                assertEquals(p.next.toString(), "urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRunsForValidationStamp:1,20,10")
                assertEquals(10, p.offset)
                assertEquals(10, p.limit)
                assertEquals(30, p.total)
            }
        }
    }

    @Test
    fun `getValidationRunsForValidationStamp end page`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(generateRuns(30))
        val resources = controller.getValidationRunsForValidationStamp(ID.of(1), 20, 10)
        assertNotNull(resources) {
            assertEquals(10, it.resources.size)
            assertNotNull(it.pagination) { p ->
                assertEquals(p.prev.toString(), "urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRunsForValidationStamp:1,10,10")
                assertNull(p.next)
                assertEquals(20, p.offset)
                assertEquals(10, p.limit)
                assertEquals(30, p.total)
            }
        }
    }

    @Test
    fun `getValidationRunsForValidationStamp one page only, less results than required`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(generateRuns(8))
        val resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assertNotNull(resources) {
            assertEquals(8, it.resources.size)
            assertNotNull(it.pagination) { p ->
                assertNull(p.prev)
                assertNull(p.next)
                assertEquals(0, p.offset)
                assertEquals(10, p.limit)
                assertEquals(8, p.total)
            }
        }
    }

    @Test
    fun `getValidationRunsForValidationStamp one page only, exact match`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(generateRuns(10))
        val resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assertNotNull(resources) {
            assertEquals(10, it.resources.size)
            assertNotNull(it.pagination) { p ->
                assertNull(p.prev)
                assertNull(p.next)
                assertEquals(0, p.offset)
                assertEquals(10, p.limit)
                assertEquals(10, p.total)
            }
        }
    }

    @Test(expected = PaginationOffsetException::class)
    fun `getValidationRunsForValidationStamp negative offset`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(generateRuns(10))
        controller.getValidationRunsForValidationStamp(ID.of(1), -1, 10)
    }

    @Test(expected = PaginationOffsetException::class)
    fun `getValidationRunsForValidationStamp offset greater than the total`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(generateRuns(10))
        controller.getValidationRunsForValidationStamp(ID.of(1), 10, 10)
    }

    @Test(expected = PaginationCountException::class)
    fun `getValidationRunsForValidationStamp count 0`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(generateRuns(10))
        controller.getValidationRunsForValidationStamp(ID.of(1), 0, 0)
    }

    @Test(expected = PaginationCountException::class)
    fun `getValidationRunsForValidationStamp negative count`() {
        whenever(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Int.MAX_VALUE)).thenReturn(generateRuns(10))
        controller.getValidationRunsForValidationStamp(ID.of(1), 0, -1)
    }

}