package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.MultiSelection
import net.nemerosa.ontrack.model.structure.*
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.json.JsonUtils.fromMap
import static net.nemerosa.ontrack.json.JsonUtils.mapToJson
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
        type = new AutoPromotionPropertyType(
                new GeneralExtensionFeature(),
                structureService
        )
    }

    @Test
    void 'Edition form'() {
        when(structureService.getValidationStampListForBranch(branch.getId())).thenReturn([validationStamp1, validationStamp2])
        def form = type.getEditionForm(
                promotionLevel,
                new AutoPromotionProperty([validationStamp1], '', '', []))
        def field = form.getField('validationStamps') as MultiSelection
        assert field.items.collect { it.name } == ['VS1', 'VS2']
        assert field.items.collect { it.selected } == [true, false]
    }

    @Test(expected = AutoPromotionPropertyCannotParseException)
    void 'From client - parsing error'() {
        type.fromClient(mapToJson([
                validationStamps: 'VS1'
        ]))
    }

    @Test
    void 'From storage'() {
        when(structureService.getValidationStamp(ID.of(1))).thenReturn(validationStamp1)
        when(structureService.getValidationStamp(ID.of(2))).thenReturn(validationStamp2)
        def autoPromotionProperty = type.fromStorage(fromMap([
                validationStamps: [1, 2],
                include: 'include',
                exclude: 'exclude',
        ]))
        assert autoPromotionProperty.validationStamps.collect { it.name } == ['VS1', 'VS2']
        assert autoPromotionProperty.include == 'include'
        assert autoPromotionProperty.exclude == 'exclude'
    }

    @Test
    void 'From storage - backward compatibility'() {
        when(structureService.getValidationStamp(ID.of(1))).thenReturn(validationStamp1)
        when(structureService.getValidationStamp(ID.of(2))).thenReturn(validationStamp2)
        def autoPromotionProperty = type.fromStorage(
                JsonUtils.intArray(1, 2)
        )
        assert autoPromotionProperty.validationStamps.collect { it.name } == ['VS1', 'VS2']
        assert autoPromotionProperty.include == ''
        assert autoPromotionProperty.exclude == ''
    }

    @Test
    void 'For storage'() {
        def autoPromotionProperty = new AutoPromotionProperty(
                [validationStamp1, validationStamp2],
                'include',
                'exclude',
                [],
        )
        def node = type.forStorage(autoPromotionProperty)
        assert node.path('validationStamps')[0].asInt() == 1
        assert node.path('validationStamps')[1].asInt() == 2
        assert node.path('include').asText() == 'include'
        assert node.path('exclude').asText() == 'exclude'
    }

    @Test
    void 'For and from storage'() {
        when(structureService.getValidationStamp(ID.of(1))).thenReturn(validationStamp1)
        when(structureService.getValidationStamp(ID.of(2))).thenReturn(validationStamp2)
        def autoPromotionProperty = new AutoPromotionProperty(
                [validationStamp1, validationStamp2],
                'include',
                'exclude',
                []
        )
        def node = type.forStorage(autoPromotionProperty)
        def restored = type.fromStorage(node)
        assert restored == autoPromotionProperty
    }

    @Test
    void 'From client'() {
        when(structureService.getValidationStamp(ID.of(1))).thenReturn(validationStamp1)
        when(structureService.getValidationStamp(ID.of(2))).thenReturn(validationStamp2)
        def autoPromotionProperty = type.fromClient(fromMap([
                validationStamps: [1, 2]
        ]))
        assert autoPromotionProperty.validationStamps.collect { it.name } == ['VS1', 'VS2']
    }

}
