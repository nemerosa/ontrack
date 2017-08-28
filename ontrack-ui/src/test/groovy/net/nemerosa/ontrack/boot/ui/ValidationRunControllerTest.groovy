package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.PaginationCountException
import net.nemerosa.ontrack.ui.resource.PaginationOffsetException
import net.nemerosa.ontrack.ui.resource.Resources
import org.junit.Before
import org.junit.Test

import java.lang.reflect.Field

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ValidationRunControllerTest {

    private ValidationRunController controller
    private StructureService structureService
    private PropertyService propertyService
    private PredefinedValidationStampService predefinedValidationStampService
    private SecurityService securityService
    private final Branch branch = Branch.of(
            Project.of(nd("P", "Project")).withId(ID.of(1)),
            nd("B", "Branch")
    ).withId(ID.of(1))
    private final ValidationStamp validationStamp = ValidationStamp.of(
            branch,
            nd('VS', 'Validation stamp')
    ).withId(ID.of(1))

    @Before
    void before() {
        structureService = mock(StructureService.class)
        ValidationRunStatusService validationRunStatusService = mock(ValidationRunStatusService.class)
        propertyService = mock(PropertyService.class)
        securityService = mock(SecurityService.class)
        predefinedValidationStampService = mock(PredefinedValidationStampService)
        controller = new ValidationRunController(
                structureService,
                validationRunStatusService, validationDataTypeService,
                propertyService,
                securityService
        )
        // Mock URI builder for tests
        Field field = AbstractResourceController.class.getDeclaredField("uriBuilder")
        field.setAccessible(true)
        field.set(controller, new MockURIBuilder())
    }

    @Test
    void 'getValidationRunsForValidationStamp: no result'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(Collections.emptyList())
        Resources<ValidationRun> resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assert resources != null
        assert resources.resources.empty
        assert resources.pagination != null
        assert resources.pagination.prev == null
        assert resources.pagination.next == null
        assert resources.pagination.offset == 0
        assert resources.pagination.limit == 10
        assert resources.pagination.total == 0
    }

    @Test
    void 'getValidationRunsForValidationStamp: first page'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(generateRuns(30))
        Resources<ValidationRun> resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assert resources != null
        assert resources.resources.size() == 10
        assert resources.pagination != null
        assert resources.pagination.prev == null
        assert resources.pagination.next.toString() == 'urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRunsForValidationStamp:1,10,10'
        assert resources.pagination.offset == 0
        assert resources.pagination.limit == 10
        assert resources.pagination.total == 30
    }

    @Test
    void 'getValidationRunsForValidationStamp: middle page'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(generateRuns(30))
        Resources<ValidationRun> resources = controller.getValidationRunsForValidationStamp(ID.of(1), 10, 10)
        assert resources != null
        assert resources.resources.size() == 10
        assert resources.pagination != null
        assert resources.pagination.prev.toString() == 'urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRunsForValidationStamp:1,0,10'
        assert resources.pagination.next.toString() == 'urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRunsForValidationStamp:1,20,10'
        assert resources.pagination.offset == 10
        assert resources.pagination.limit == 10
        assert resources.pagination.total == 30
    }

    @Test
    void 'getValidationRunsForValidationStamp: end page'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(generateRuns(30))
        Resources<ValidationRun> resources = controller.getValidationRunsForValidationStamp(ID.of(1), 20, 10)
        assert resources != null
        assert resources.resources.size() == 10
        assert resources.pagination != null
        assert resources.pagination.prev.toString() == 'urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRunsForValidationStamp:1,10,10'
        assert resources.pagination.next == null
        assert resources.pagination.offset == 20
        assert resources.pagination.limit == 10
        assert resources.pagination.total == 30
    }

    @Test
    void 'getValidationRunsForValidationStamp: one page only, less results than required'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(generateRuns(8))
        Resources<ValidationRun> resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assert resources != null
        assert resources.resources.size() == 8
        assert resources.pagination != null
        assert resources.pagination.prev == null
        assert resources.pagination.next == null
        assert resources.pagination.offset == 0
        assert resources.pagination.limit == 10
        assert resources.pagination.total == 8
    }

    @Test
    void 'getValidationRunsForValidationStamp: one page only, exact match'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(generateRuns(10))
        Resources<ValidationRun> resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assert resources != null
        assert resources.resources.size() == 10
        assert resources.pagination != null
        assert resources.pagination.prev == null
        assert resources.pagination.next == null
        assert resources.pagination.offset == 0
        assert resources.pagination.limit == 10
        assert resources.pagination.total == 10
    }

    @Test(expected = PaginationOffsetException)
    void 'getValidationRunsForValidationStamp: negative offset'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(generateRuns(10))
        controller.getValidationRunsForValidationStamp(ID.of(1), -1, 10)
    }

    @Test(expected = PaginationOffsetException)
    void 'getValidationRunsForValidationStamp: offset greater than the total'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(generateRuns(10))
        controller.getValidationRunsForValidationStamp(ID.of(1), 10, 10)
    }

    @Test(expected = PaginationCountException)
    void 'getValidationRunsForValidationStamp: count 0'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(generateRuns(10))
        controller.getValidationRunsForValidationStamp(ID.of(1), 0, 0)
    }

    @Test(expected = PaginationCountException)
    void 'getValidationRunsForValidationStamp: negative count'() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(generateRuns(10))
        controller.getValidationRunsForValidationStamp(ID.of(1), 0, -1)
    }

    List<ValidationRun> generateRuns(int count) {
        (1..count).collect {
            def build = Build.of(
                    branch,
                    nd("$it", "Build $it"),
                    Signature.of("user")
            ).withId(ID.of(it))
            ValidationRun.of(
                    build,
                    validationStamp,
                    it,
                    Signature.of('user'),
                    ValidationRunStatusID.STATUS_PASSED,
                    ''
            )
        }
    }
}