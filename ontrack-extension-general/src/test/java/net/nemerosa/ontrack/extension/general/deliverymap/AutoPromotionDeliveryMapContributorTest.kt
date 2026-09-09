package net.nemerosa.ontrack.extension.general.deliverymap

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.general.AutoPromotionProperty
import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.deliverymap.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AutoPromotionDeliveryMapContributorTest {

    private val branch = Branch.of(
        Project.of(nd("P", "")).withId(ID.of(1)),
        nd("B", "")
    ).withId(ID.of(1))

    private val bronze = PromotionLevel.of(branch, nd("BRONZE", "")).withId(ID.of(1))
    private val silver = PromotionLevel.of(branch, nd("SILVER", "")).withId(ID.of(2))

    private val quality = ValidationStamp.of(branch, nd("QUALITY", "")).withId(ID.of(1))
    private val security = ValidationStamp.of(branch, nd("SECURITY", "")).withId(ID.of(2))
    private val ciSmoke = ValidationStamp.of(branch, nd("CI-SMOKE", "")).withId(ID.of(3))

    private lateinit var structureService: StructureService
    private lateinit var propertyService: PropertyService
    private lateinit var checkpointFactory: DeliveryMapCheckpointFactory
    private lateinit var contributor: AutoPromotionDeliveryMapContributor

    @BeforeEach
    fun setup() {
        structureService = mockk()
        every { structureService.getPromotionLevelListForBranch(branch.id) } returns listOf(bronze, silver)
        every { structureService.getValidationStampListForBranch(branch.id) } returns
                listOf(quality, security, ciSmoke)
        propertyService = mockk()
        every { propertyService.getPropertyValue(any(), AutoPromotionPropertyType::class.java) } returns null
        checkpointFactory = mockk<DeliveryMapCheckpointFactory>().apply {
            every { validationStamp(any()) } answers {
                val vs = firstArg<ValidationStamp>()
                DeliveryMapCheckpoint(
                    id = DeliveryMapCheckpointTypes.validationStamp(vs.id),
                    type = DeliveryMapCheckpointTypes.VALIDATION_STAMP,
                    name = vs.name,
                )
            }
        }
        contributor = AutoPromotionDeliveryMapContributor(
            structureService = structureService,
            propertyService = propertyService,
            checkpointFactory = checkpointFactory,
        )
    }

    private fun autoPromotion(
        promotionLevel: PromotionLevel,
        validationStamps: List<ValidationStamp> = emptyList(),
        include: String = "",
        exclude: String = "",
        promotionLevels: List<PromotionLevel> = emptyList(),
    ) {
        every { propertyService.getPropertyValue(promotionLevel, AutoPromotionPropertyType::class.java) } returns
                AutoPromotionProperty(
                    validationStamps = validationStamps,
                    include = include,
                    exclude = exclude,
                    promotionLevels = promotionLevels,
                )
    }

    private fun contribute() = contributor.contribute(branch)

    @Test
    fun `No auto promotion anywhere contributes nothing`() {
        val contribution = contribute()
        assertEquals(emptyList(), contribution.checkpoints)
        assertEquals(emptyList(), contribution.edges)
    }

    @Test
    fun `An explicitly named validation stamp unlocks its promotion level`() {
        autoPromotion(silver, validationStamps = listOf(quality))
        val contribution = contribute()
        assertEquals(listOf("validation-stamp:1"), contribution.checkpoints.map { it.id })
        val edge = contribution.edges.single()
        assertEquals(DeliveryMapEdgeKind.UNLOCKS, edge.kind)
        assertEquals("validation-stamp:1", edge.source)
        assertEquals("promotion-level:2", edge.target)
    }

    @Test
    fun `Only the validation stamps taking part in an edge are on the map`() {
        // SECURITY and CI-SMOKE are stamps of the branch which no promotion depends on
        autoPromotion(silver, validationStamps = listOf(quality))
        assertEquals(listOf("QUALITY"), contribute().checkpoints.map { it.name })
    }

    @Test
    fun `A promotion level unlocks another promotion level`() {
        autoPromotion(silver, promotionLevels = listOf(bronze))
        val edge = contribute().edges.single()
        assertEquals(DeliveryMapEdgeKind.UNLOCKS, edge.kind)
        assertEquals("promotion-level:1", edge.source)
        assertEquals("promotion-level:2", edge.target)
    }

    @Test
    fun `A promotion level is not contributed as a checkpoint, only reached as one`() {
        // Promotion level checkpoints come from the core, which puts every one of them on the map
        autoPromotion(silver, promotionLevels = listOf(bronze))
        assertEquals(emptyList(), contribute().checkpoints)
    }

    @Test
    fun `Stamps selected by a pattern collapse into one aggregate checkpoint labelled with the pattern`() {
        autoPromotion(silver, include = ".*")
        val contribution = contribute()
        val aggregate = contribution.checkpoints.single()
        assertEquals("validation-stamp-pattern:2", aggregate.id)
        assertEquals(DeliveryMapCheckpointTypes.VALIDATION_STAMP_PATTERN, aggregate.type)
        assertEquals(".*", aggregate.name)
        assertEquals(
            ValidationStampPatternCheckpointData(promotionLevelId = 2, include = ".*", exclude = ""),
            aggregate.data.parse<ValidationStampPatternCheckpointData>(),
        )
        // One edge, not one per stamp: an include of ".*" on a branch with forty stamps would
        // otherwise draw forty edges converging on one promotion
        assertEquals(1, contribution.edges.size)
        assertEquals("validation-stamp-pattern:2", contribution.edges.single().source)
    }

    @Test
    fun `An aggregate checkpoint stands for the stamps the pattern matches`() {
        autoPromotion(silver, include = ".*", exclude = "CI-.*")
        val aggregate = contribute().checkpoints.single()
        assertEquals(listOf("QUALITY", "SECURITY"), aggregate.members.map { it.name })
    }

    @Test
    fun `An aggregate checkpoint has no arrival of its own`() {
        // Its members carry the arrivals; what "arriving at forty stamps at once" means is not
        // something the configuration says
        autoPromotion(silver, include = ".*")
        assertNull(contribute().checkpoints.single().arrival)
    }

    @Test
    fun `A promotion naming its stamps explicitly gets one checkpoint each`() {
        autoPromotion(silver, validationStamps = listOf(quality, security))
        val contribution = contribute()
        assertEquals(listOf("validation-stamp:1", "validation-stamp:2"), contribution.checkpoints.map { it.id })
        assertTrue(contribution.checkpoints.all { it.members.isEmpty() })
        assertEquals(2, contribution.edges.size)
    }

    @Test
    fun `A stamp named explicitly stays its own checkpoint even when the pattern also matches it`() {
        // Naming a stamp is the more specific statement of the two
        autoPromotion(silver, validationStamps = listOf(quality), include = ".*")
        val contribution = contribute()
        assertEquals(
            listOf("validation-stamp:1", "validation-stamp-pattern:2"),
            contribution.checkpoints.map { it.id },
        )
        assertEquals(listOf("SECURITY", "CI-SMOKE"), contribution.checkpoints[1].members.map { it.name })
    }

    @Test
    fun `A pattern matching no stamp draws no aggregate checkpoint`() {
        // Configuration pointing at nothing is the subject of #1705, not something to half-draw here
        autoPromotion(silver, include = "NO-SUCH-.*")
        val contribution = contribute()
        assertEquals(emptyList(), contribution.checkpoints)
        assertEquals(emptyList(), contribution.edges)
    }

    @Test
    fun `Each promotion level gets its own aggregate checkpoint`() {
        autoPromotion(bronze, include = "QUALITY")
        autoPromotion(silver, include = "SECURITY")
        val contribution = contribute()
        assertEquals(
            listOf("validation-stamp-pattern:1", "validation-stamp-pattern:2"),
            contribution.checkpoints.map { it.id },
        )
    }

    @Test
    fun `A stamp selected by two promotion levels is only read once`() {
        // Building a stamp checkpoint costs a query for its latest run, and the same stamp is
        // routinely selected by several promotions - by name for one and by pattern for another
        autoPromotion(bronze, validationStamps = listOf(quality))
        autoPromotion(silver, include = "QUALITY")
        contribute()
        verify(exactly = 1) { checkpointFactory.validationStamp(quality) }
    }

    @Test
    fun `A promotion level named by an auto promotion but absent from the branch draws no edge`() {
        val gold = PromotionLevel.of(branch, nd("GOLD", "")).withId(ID.of(99))
        autoPromotion(silver, promotionLevels = listOf(gold))
        assertEquals(emptyList(), contribute().edges)
    }

}
