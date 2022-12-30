package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService
import net.nemerosa.ontrack.repository.CoreBuildFilterRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StandardBuildFilterProviderTest {

    private lateinit var provider: StandardBuildFilterProvider

    @BeforeEach
    fun before() {
        val structureService = mock(StructureService::class.java)
        val validationRunStatusService = mock(ValidationRunStatusService::class.java)
        val propertyService = mock(PropertyService::class.java)
        val coreBuildFilterRepository = mock(CoreBuildFilterRepository::class.java)
        provider = StandardBuildFilterProvider(
            structureService,
            validationRunStatusService,
            propertyService,
            coreBuildFilterRepository
        )
    }

    @Test
    fun `Parsing default`() {
        val data = provider.parse(
                emptyMap<String,String>().asJson()
        )
        assertNotNull(data) {
            assertEquals(10, it.count)
        }
    }

    @Test
    fun `Parsing without count`() {
        val data = provider.parse(
                mapOf(
                        "withPromotionLevel" to "IRON",
                ).asJson()
        )
        assertNotNull(data) {
            assertEquals(10, it.count)
            assertEquals("IRON", it.withPromotionLevel)
        }
    }
    @Test
    fun parse_count_only() {
        val data = provider.parse(
            mapOf("count" to 5).asJson()
        )
        assertNotNull(data) {
            assertEquals(5, it.count)
            assertNull(it.withPromotionLevel)
        }
    }

    @Test
    fun parse_with_promotion_level_null() {
        val data = provider.parse(
            mapOf(
                "count" to 5,
                "withPromotionLevel" to null,
            ).asJson()
        )
        assertNotNull(data) {
            assertEquals(5, it.count)
            assertNull(it.withPromotionLevel)
        }
    }

    @Test
    fun parse_with_after_date_null() {
        val data = provider.parse(
            mapOf(
                "count" to 5,
                "afterDate" to null,
            ).asJson()
        )
        assertNotNull(data) {
            assertEquals(5, it.count)
            assertNull(it.afterDate)
        }
    }

}