package net.nemerosa.ontrack.extension.general

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class PreviousPromotionConditionServiceImplTest {

    private val project = Project.of(nd("P", "")).withId(ID.of(1))
    private val branch = Branch.of(project, nd("B", "")).withId(ID.of(1))
    private val silver = PromotionLevel.of(branch, nd("SILVER", "")).withId(ID.of(2))

    private lateinit var propertyService: PropertyService
    private lateinit var cachedSettingsService: CachedSettingsService
    private lateinit var service: PreviousPromotionConditionServiceImpl

    @BeforeEach
    fun setup() {
        propertyService = mockk()
        every {
            propertyService.getPropertyValue(any(), PreviousPromotionConditionPropertyType::class.java)
        } returns null
        cachedSettingsService = mockk()
        settings(false)
        service = PreviousPromotionConditionServiceImpl(propertyService, cachedSettingsService)
    }

    private fun settings(required: Boolean) {
        every {
            cachedSettingsService.getCachedSettings(PreviousPromotionConditionSettings::class.java)
        } returns PreviousPromotionConditionSettings(previousPromotionRequired = required)
    }

    private fun property(entity: ProjectEntity, required: Boolean) {
        every {
            propertyService.getPropertyValue(entity, PreviousPromotionConditionPropertyType::class.java)
        } returns PreviousPromotionConditionProperty(previousPromotionRequired = required)
    }

    private fun resolve() = service.resolvePreviousPromotionCondition(silver)

    @Test
    fun `Nothing set anywhere falls back to the global settings`() {
        val resolution = resolve()
        assertEquals(false, resolution.required)
        assertNull(resolution.source, "The global settings are not an entity")
    }

    @Test
    fun `The global settings decide when nothing else does`() {
        settings(true)
        val resolution = resolve()
        assertEquals(true, resolution.required)
        assertNull(resolution.source)
    }

    @Test
    fun `The property on the promotion level decides`() {
        property(silver, true)
        val resolution = resolve()
        assertEquals(true, resolution.required)
        assertSame(silver, resolution.source)
    }

    @Test
    fun `The property on the branch decides`() {
        property(branch, true)
        val resolution = resolve()
        assertEquals(true, resolution.required)
        assertSame(branch, resolution.source)
    }

    @Test
    fun `The property on the project decides`() {
        property(project, true)
        val resolution = resolve()
        assertEquals(true, resolution.required)
        assertSame(project, resolution.source)
    }

    @Test
    fun `First found wins - a false on the promotion level overrides a true on the project`() {
        // The point of the cascade: a false is an answer, not an absence
        property(project, true)
        property(silver, false)
        val resolution = resolve()
        assertEquals(false, resolution.required)
        assertSame(silver, resolution.source)
    }

    @Test
    fun `First found wins - a false on the branch overrides the global settings`() {
        settings(true)
        property(branch, false)
        val resolution = resolve()
        assertEquals(false, resolution.required)
        assertSame(branch, resolution.source)
    }

    @Test
    fun `The promotion level comes before the branch, which comes before the project`() {
        property(project, false)
        property(branch, false)
        property(silver, true)
        assertSame(silver, resolve().source)
    }

}
