package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
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
        PropertyService propertyService = mock(PropertyService.class)
        SecurityService securityService = mock(SecurityService.class)
        controller = new ValidationRunController(
                structureService,
                validationRunStatusService,
                propertyService,
                securityService
        )
        // Mock URI builder for tests
        Field field = AbstractResourceController.class.getDeclaredField("uriBuilder")
        field.setAccessible(true)
        field.set(controller, new MockURIBuilder())
    }

    @Test
    void getValidationRunsForValidationStamp_none() throws Exception {
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
    void getValidationRunsForValidationStamp_first_page() throws Exception {
        def runs = generateRuns(15)
        assert runs.size() == 15
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, Integer.MAX_VALUE)).thenReturn(runs)
        Resources<ValidationRun> resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assert resources != null
        assert resources.resources.size() == 10
        assert resources.pagination != null
        assert resources.pagination.prev == null
        assert resources.pagination.next.toString() == 'urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#getValidationRunsForValidationStamp:1,10,10'
        assert resources.pagination.offset == 0
        assert resources.pagination.limit == 10
        assert resources.pagination.total == 15
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