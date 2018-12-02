package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService
import net.nemerosa.ontrack.repository.CoreBuildFilterRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class StandardBuildFilterProviderTest {

    private var provider: StandardBuildFilterProvider? = null

    @Before
    fun before() {
        val structureService = mock(StructureService::class.java)
        val validationRunStatusService = mock(ValidationRunStatusService::class.java)
        val propertyService = mock(PropertyService::class.java)
        val coreBuildFilterRepository = mock(CoreBuildFilterRepository::class.java)
        provider = StandardBuildFilterProvider(
                structureService,
                validationRunStatusService,
                propertyService,
                coreBuildFilterRepository)
    }

    @Test
    fun parse_count_only() {
        val data = provider!!.parse(JsonUtils.`object`().with("count", 5).end())
        assertTrue(data.isPresent)
        assertEquals(5, data.get().count.toLong())
        assertNull(data.get().withPromotionLevel)
    }

    @Test
    fun parse_with_promotion_level_null() {
        val data = provider!!.parse(JsonUtils.`object`()
                .with("count", 5)
                .with("withPromotionLevel", null as String?)
                .end())
        assertTrue(data.isPresent)
        assertEquals(5, data.get().count.toLong())
        assertNull(data.get().withPromotionLevel)
    }

    @Test
    fun parse_with_after_date_null() {
        val data = provider!!.parse(JsonUtils.`object`()
                .with("count", 5)
                .withNull("afterDate")
                .end())
        assertTrue(data.isPresent)
        assertEquals(5, data.get().count.toLong())
        assertNull(data.get().afterDate)
    }

}