package net.nemerosa.ontrack.service.deliverymap

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.deliverymap.DeliveryMapCheckpointTypes
import net.nemerosa.ontrack.model.deliverymap.PromotionLevelCheckpointData
import net.nemerosa.ontrack.model.deliverymap.ValidationStampCheckpointData
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeliveryMapCheckpointFactoryImplTest {

    private val branch = Branch.of(
        Project.of(nd("P", "")).withId(ID.of(1)),
        nd("B", "")
    ).withId(ID.of(1))

    private val build = Build.of(branch, nd("1", ""), Signature.of("test")).withId(ID.of(10))

    private val bronze = PromotionLevel.of(branch, nd("BRONZE", "Bronze level")).withId(ID.of(1))
    private val quality = ValidationStamp.of(branch, nd("QUALITY", "Quality checks")).withId(ID.of(2))

    private val time: LocalDateTime = LocalDateTime.of(2026, 9, 1, 10, 0)

    private lateinit var structureService: StructureService
    private lateinit var factory: DeliveryMapCheckpointFactoryImpl

    @BeforeEach
    fun setup() {
        structureService = mockk()
        factory = DeliveryMapCheckpointFactoryImpl(structureService)
    }

    @Test
    fun `Promotion level checkpoint carries a namespaced id and the promotion level payload`() {
        every { structureService.getLastPromotionRunForPromotionLevel(bronze) } returns null
        val checkpoint = factory.promotionLevel(bronze)
        assertEquals("promotion-level:1", checkpoint.id)
        assertEquals(DeliveryMapCheckpointTypes.PROMOTION_LEVEL, checkpoint.type)
        assertEquals("BRONZE", checkpoint.name)
        assertEquals("Bronze level", checkpoint.description)
        assertEquals(
            PromotionLevelCheckpointData(promotionLevelId = 1, image = false),
            checkpoint.data.parse<PromotionLevelCheckpointData>(),
        )
    }

    @Test
    fun `A promotion level nothing has been promoted to has no arrival`() {
        every { structureService.getLastPromotionRunForPromotionLevel(bronze) } returns null
        assertNull(factory.promotionLevel(bronze).arrival)
    }

    @Test
    fun `A promotion level is arrived at by being promoted, and has no status of its own`() {
        // Arriving *is* the outcome on a promotion level, unlike on a validation stamp
        every { structureService.getLastPromotionRunForPromotionLevel(bronze) } returns PromotionRun(
            id = ID.of(100),
            build = build,
            promotionLevel = bronze,
            signature = Signature.of(time, "test"),
            description = null,
        )
        val arrival = factory.promotionLevel(bronze).arrival
        assertEquals(build, arrival?.build)
        assertEquals(time, arrival?.time)
        assertNull(arrival?.status)
    }

    @Test
    fun `Validation stamp checkpoint carries a namespaced id and the validation stamp payload`() {
        every { structureService.getValidationRunsForValidationStamp(quality, 0, 1) } returns emptyList()
        val checkpoint = factory.validationStamp(quality)
        assertEquals("validation-stamp:2", checkpoint.id)
        assertEquals(DeliveryMapCheckpointTypes.VALIDATION_STAMP, checkpoint.type)
        assertEquals("QUALITY", checkpoint.name)
        assertEquals("Quality checks", checkpoint.description)
        assertEquals(
            ValidationStampCheckpointData(validationStampId = 2, image = false),
            checkpoint.data.parse<ValidationStampCheckpointData>(),
        )
    }

    @Test
    fun `A validation stamp never run has no arrival`() {
        every { structureService.getValidationRunsForValidationStamp(quality, 0, 1) } returns emptyList()
        assertNull(factory.validationStamp(quality).arrival)
    }

    @Test
    fun `A validation stamp shows the build which arrived and failed`() {
        // Arriving is not the same as succeeding, and only a validation stamp can show the difference
        every { structureService.getValidationRunsForValidationStamp(quality, 0, 1) } returns listOf(
            validationRun(ValidationRunStatusID.STATUS_FAILED)
        )
        val arrival = factory.validationStamp(quality).arrival
        assertEquals(build, arrival?.build)
        assertEquals(time, arrival?.time)
        assertEquals(ValidationRunStatusID.FAILED, arrival?.status?.id)
    }

    @Test
    fun `A validation stamp shows the status of its latest run`() {
        every { structureService.getValidationRunsForValidationStamp(quality, 0, 1) } returns listOf(
            validationRun(ValidationRunStatusID.STATUS_PASSED)
        )
        assertEquals(
            ValidationRunStatusID.PASSED,
            factory.validationStamp(quality).arrival?.status?.id,
        )
    }

    private fun validationRun(status: ValidationRunStatusID) = ValidationRun(
        id = ID.of(200),
        build = build,
        validationStamp = quality,
        runOrder = 1,
        data = null,
        validationRunStatuses = listOf(
            ValidationRunStatus(
                ID.of(300),
                Signature.of(time, "test"),
                status,
                null,
            )
        ),
    )

}
