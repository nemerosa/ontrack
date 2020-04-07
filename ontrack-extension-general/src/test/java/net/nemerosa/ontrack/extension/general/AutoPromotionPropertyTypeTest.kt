package net.nemerosa.ontrack.extension.general

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.form.MultiSelection
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AutoPromotionPropertyTypeTest {

    private lateinit var type: AutoPromotionPropertyType 
    private lateinit var structureService: StructureService 

    private val branch = Branch.of(
            Project.of(nd("P", "")).withId(ID.of(1)),
            nd("B", "")
    ).withId(ID.of(1))

    private val promotionLevel = PromotionLevel.of(
            branch,
            nd("PL", "")
    ).withId(ID.of(1))

    private val validationStamp1 = ValidationStamp.of(
            branch,
            nd("VS1", "")
    ).withId(ID.of(1))

    private val validationStamp2 = ValidationStamp.of(
            branch,
            nd("VS2", "")
    ).withId(ID.of(2))

    @Before
    fun setup() {
        structureService = mock()
        type = AutoPromotionPropertyType(
                GeneralExtensionFeature(),
                structureService
        )
    }

    @Test
    fun `Edition form`() {
        whenever(structureService.getValidationStampListForBranch(branch.id)).thenReturn(listOf(validationStamp1, validationStamp2))
        val form = type.getEditionForm(
                promotionLevel,
                AutoPromotionProperty(listOf(validationStamp1), "", ""))
        val field = form.getField("validationStamps") as MultiSelection
        assertEquals(listOf("VS1", "VS2"), field.items.map { it.name })
        assertEquals(listOf(true, false), field.items.map { it.isSelected })
    }

    @Test(expected = AutoPromotionPropertyCannotParseException::class)
    fun `From client - parsing error`() {
        type.fromClient(mapOf(
                "validationStamps" to "VS1"
        ).asJson())
    }

    @Test
    fun `From storage`() {
        whenever(structureService.getValidationStamp(ID.of(1))).thenReturn(validationStamp1)
        whenever(structureService.getValidationStamp(ID.of(2))).thenReturn(validationStamp2)
        val autoPromotionProperty = type.fromStorage(mapOf(
                "validationStamps" to listOf(1, 2),
                "include" to "include",
                "exclude" to "exclude"
        ).asJson())
        assertEquals(listOf("VS1", "VS2"), autoPromotionProperty.validationStamps.map { it.name })
        assertEquals("include", autoPromotionProperty.include)
        assertEquals("exclude", autoPromotionProperty.exclude)
    }

    @Test
    fun `From storage - backward compatibility`() {
        whenever(structureService.getValidationStamp(ID.of(1))).thenReturn(validationStamp1)
        whenever(structureService.getValidationStamp(ID.of(2))).thenReturn(validationStamp2)
        val autoPromotionProperty = type.fromStorage(
                JsonUtils.intArray(1, 2)
        )
        assertEquals(listOf("VS1", "VS2"), autoPromotionProperty.validationStamps.map { it.name })
        assertEquals("", autoPromotionProperty.include)
        assertEquals("", autoPromotionProperty.exclude)
    }

    @Test
    fun `For storage`() {
        val autoPromotionProperty = AutoPromotionProperty(
                listOf(validationStamp1, validationStamp2),
                "include",
                "exclude"
        )
        val node = type.forStorage(autoPromotionProperty)
        assertEquals(1, node.path("validationStamps")[0].asInt())
        assertEquals(2, node.path("validationStamps")[1].asInt())
        assertEquals("include", node.path("include").asText())
        assertEquals("exclude", node.path("exclude").asText())
    }

    @Test
    fun `For and from storage`() {
        whenever(structureService.getValidationStamp(ID.of(1))).thenReturn(validationStamp1)
        whenever(structureService.getValidationStamp(ID.of(2))).thenReturn(validationStamp2)
        val autoPromotionProperty = AutoPromotionProperty(
                listOf(validationStamp1, validationStamp2),
                "include",
                "exclude"
        )
        val node = type.forStorage(autoPromotionProperty)
        val restored = type.fromStorage(node)
        assertEquals(autoPromotionProperty, restored)
    }

    @Test
    fun `From client`() {
        whenever(structureService.getValidationStamp(ID.of(1))).thenReturn(validationStamp1)
        whenever(structureService.getValidationStamp(ID.of(2))).thenReturn(validationStamp2)
        val autoPromotionProperty = type.fromClient(mapOf(
                "validationStamps" to listOf(1, 2)
        ).asJson())
        assertEquals(listOf("VS1", "VS2"), autoPromotionProperty.validationStamps.map { it.name })
    }

}
