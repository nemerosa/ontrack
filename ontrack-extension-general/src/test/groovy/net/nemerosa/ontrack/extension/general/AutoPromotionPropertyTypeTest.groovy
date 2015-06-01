package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.form.MultiSelection
import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class AutoPromotionPropertyTypeTest {

    private AutoPromotionPropertyType type
    private StructureService structureService

    private Branch branch = Branch.of(
            Project.of(nd('P', '')).withId(ID.of(1)),
            nd('B', '')
    ).withId(ID.of(1))

    private final PromotionLevel promotionLevel = PromotionLevel.of(
            branch,
            nd('PL', '')
    ).withId(ID.of(1))

    private final ValidationStamp validationStamp1 = ValidationStamp.of(
            branch,
            nd('VS1', '')
    ).withId(ID.of(1))

    private final ValidationStamp validationStamp2 = ValidationStamp.of(
            branch,
            nd('VS2', '')
    ).withId(ID.of(2))

    @Before
    void 'Setup'() {
        structureService = mock(StructureService)
        type = new AutoPromotionPropertyType(structureService)
    }

    @Test
    void 'Edition form'() {
        when(structureService.getValidationStampListForBranch(branch.getId())).thenReturn([validationStamp1, validationStamp2])
        def form = type.getEditionForm(
                promotionLevel,
                new AutoPromotionProperty([validationStamp1]))
        def field = form.getField('validationStamps') as MultiSelection
        assert field.items.collect { it.name } == ['VS1', 'VS2']
        assert field.items.collect { it.selected } == [true, false]
    }

}
