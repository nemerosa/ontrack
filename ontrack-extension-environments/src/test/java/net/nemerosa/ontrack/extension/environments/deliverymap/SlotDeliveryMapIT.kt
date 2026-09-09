package net.nemerosa.ontrack.extension.environments.deliverymap

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getRequiredBooleanField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.structure.Branch
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * End to end assembly of the slot half of a branch's delivery map, from the admission rules to the
 * GraphQL field.
 */
class SlotDeliveryMapIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Every slot of the project is on the map of every branch, in environment order`() {
        asAdmin {
            val staging = slotTestSupport.slot(order = 100)
            val production = slotTestSupport.slot(order = 200, project = staging.project)
            staging.project.branch {
                val slots = deliveryMap(this).checkpoints.filter { it.getRequiredTextField("type") == "slot" }
                assertEquals(
                    listOf("slot:${staging.id}", "slot:${production.id}"),
                    slots.map { it.getRequiredTextField("id") },
                )
            }
        }
    }

    @Test
    fun `A slot with no deployment names no build`() {
        asAdmin {
            val staging = slotTestSupport.slot()
            staging.project.branch {
                val checkpoint = slotCheckpoint(deliveryMap(this), staging)
                assertNull(checkpoint.path("arrival").takeIf { !it.isNull })
            }
        }
    }

    @Test
    fun `A slot names the most recently deployed build of this branch`() {
        asAdmin {
            val staging = slotTestSupport.slot()
            val branch = staging.project.branch(name = "main")
            slotTestSupport.createRunAndFinishDeployment(branchName = "main", slot = staging)
            val latest = slotTestSupport.createRunAndFinishDeployment(branchName = "main", slot = staging)
            val checkpoint = slotCheckpoint(deliveryMap(branch), staging)
            assertEquals(latest.build.name, checkpoint.path("arrival").path("build").getRequiredTextField("name"))
            assertNull(checkpoint.path("data").path("otherBranch").takeIf { !it.isNull })
        }
    }

    @Test
    fun `A slot names the deployed build of another branch, marked as such`() {
        // The deliberate exception to ADR 0007, recorded in ADR 0009: an empty production checkpoint
        // would hide who is actually in production
        asAdmin {
            val staging = slotTestSupport.slot()
            val main = staging.project.branch(name = "main")
            val deployment = slotTestSupport.createRunAndFinishDeployment(branchName = "release-1.3", slot = staging)
            val checkpoint = slotCheckpoint(deliveryMap(main), staging)
            assertEquals(deployment.build.name, checkpoint.path("arrival").path("build").getRequiredTextField("name"))
            assertEquals("release-1.3", checkpoint.path("data").getRequiredTextField("otherBranch"))
        }
    }

    @Test
    fun `A promotion admission rule requires the promotion level of this branch`() {
        asAdmin {
            val production = slotTestSupport.slot()
            slotService.addAdmissionRuleConfig(
                SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(production, promotion = "GOLD")
            )
            production.project.branch {
                val gold = promotionLevel("GOLD")
                val map = deliveryMap(this)
                assertEquals(
                    listOf("requires:promotion-level:${gold.id}->slot:${production.id}"),
                    map.edges.map { it.getRequiredTextField("id") },
                )
            }
        }
    }

    @Test
    fun `A promotion admission rule naming no promotion level of this branch draws an unresolved checkpoint`() {
        asAdmin {
            val production = slotTestSupport.slot()
            slotService.addAdmissionRuleConfig(
                SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(production, promotion = "GOLD")
            )
            production.project.branch {
                promotionLevel("SILVER")
                val map = deliveryMap(this)
                val unresolved = unresolvedCheckpoint(map)
                assertEquals("GOLD", unresolved.getRequiredTextField("name"))
                assertEquals("promotion-level", unresolved.path("data").getRequiredTextField("reference"))
                assertNull(unresolved.path("arrival").takeIf { !it.isNull })
                assertEquals(
                    listOf("requires:unresolved:promotion-level:GOLD->slot:${production.id}"),
                    map.edges.map { it.getRequiredTextField("id") },
                )
            }
        }
    }

    @Test
    fun `An environment admission rule naming no slot of this project draws an unresolved checkpoint`() {
        asAdmin {
            val production = slotTestSupport.slot()
            slotService.addAdmissionRuleConfig(
                SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(
                    production,
                    environmentName = "integration",
                )
            )
            production.project.branch {
                val map = deliveryMap(this)
                val unresolved = unresolvedCheckpoint(map)
                assertEquals("integration", unresolved.getRequiredTextField("name"))
                assertEquals("slot", unresolved.path("data").getRequiredTextField("reference"))
                assertEquals(
                    listOf("requires:unresolved:slot:integration->slot:${production.id}"),
                    map.edges.map { it.getRequiredTextField("id") },
                )
            }
        }
    }

    @Test
    fun `An environment admission rule joins two slots`() {
        asAdmin {
            val staging = slotTestSupport.slot(order = 100)
            val production = slotTestSupport.slot(order = 200, project = staging.project)
            slotService.addAdmissionRuleConfig(
                SlotAdmissionRuleTestFixtures.testEnvironmentAdmissionRuleConfig(production, previousSlot = staging)
            )
            staging.project.branch {
                assertEquals(
                    listOf("requires:slot:${staging.id}->slot:${production.id}"),
                    deliveryMap(this).edges.map { it.getRequiredTextField("id") },
                )
            }
        }
    }

    @Test
    fun `Slots of consecutive environments are not joined without the rule`() {
        // No fallback to environment ordering, unlike the project slot graph: the map draws what was
        // configured, and its sparseness is what surfaces a slot nobody wired up
        asAdmin {
            val staging = slotTestSupport.slot(order = 100)
            slotTestSupport.slot(order = 200, project = staging.project)
            staging.project.branch {
                assertEquals(emptyList(), deliveryMap(this).edges.map { it.getRequiredTextField("id") })
            }
        }
    }

    @Test
    fun `A branch excluded by a branch pattern rule gets an unreachable slot with no build`() {
        asAdmin {
            val production = slotTestSupport.slot()
            slotService.addAdmissionRuleConfig(
                SlotAdmissionRuleTestFixtures.testBranchPatternAdmissionRuleConfig(
                    production,
                    includes = listOf("release-.*"),
                )
            )
            slotTestSupport.createRunAndFinishDeployment(branchName = "release-1.3", slot = production)
            val main = production.project.branch(name = "main")
            val checkpoint = slotCheckpoint(deliveryMap(main), production)
            assertTrue(checkpoint.path("data").getRequiredBooleanField("unreachable"), "Unreachable")
            assertNull(checkpoint.path("arrival").takeIf { !it.isNull })
        }
    }

    @Test
    fun `A branch included by a branch pattern rule keeps its slot reachable`() {
        asAdmin {
            val production = slotTestSupport.slot()
            slotService.addAdmissionRuleConfig(
                SlotAdmissionRuleTestFixtures.testBranchPatternAdmissionRuleConfig(
                    production,
                    includes = listOf("release-.*"),
                )
            )
            val release = production.project.branch(name = "release-1.3")
            val checkpoint = slotCheckpoint(deliveryMap(release), production)
            assertEquals(false, checkpoint.path("data").getRequiredBooleanField("unreachable"))
        }
    }

    @Test
    fun `A user who cannot see the slots gets a map without them`() {
        // A contributor leaves out what the user may not see, and contributes no placeholder for it
        val staging = asAdmin { slotTestSupport.slot() }
        val branch = asAdmin { staging.project.branch(name = "main") }
        asUserWithView(staging.project) {
            assertTrue(
                deliveryMap(branch).checkpoints.none { it.getRequiredTextField("type") == "slot" },
                "No slot on the map",
            )
        }
    }

    @Test
    fun `A slot hidden by permissions never renders as unresolved`() {
        // The distinction this feature lives or dies by: if a hidden checkpoint and a broken
        // configuration looked alike, "unresolved" would come to read as "probably just permissions"
        val production = asAdmin { slotTestSupport.slot() }
        asAdmin {
            slotService.addAdmissionRuleConfig(
                SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(production, promotion = "GOLD")
            )
        }
        val branch = asAdmin { production.project.branch(name = "main") }
        asUserWithView(production.project) {
            val map = deliveryMap(branch)
            assertTrue(
                map.checkpoints.none { it.getRequiredTextField("type") == "unresolved" },
                "No unresolved checkpoint on the map",
            )
            assertEquals(emptyList(), map.edges.map { it.getRequiredTextField("id") })
        }
    }

    private data class RenderedMap(
        val checkpoints: List<JsonNode>,
        val edges: List<JsonNode>,
    )

    private fun unresolvedCheckpoint(map: RenderedMap): JsonNode =
        map.checkpoints.single { it.getRequiredTextField("type") == "unresolved" }

    private fun slotCheckpoint(map: RenderedMap, slot: Slot): JsonNode =
        map.checkpoints.single { it.getRequiredTextField("id") == "slot:${slot.id}" }

    private fun deliveryMap(branch: Branch): RenderedMap =
        run(
            """
                {
                    branch(id: ${branch.id}) {
                        deliveryMap {
                            checkpoints {
                                id
                                type
                                name
                                description
                                data
                                arrival {
                                    build { name }
                                    time
                                }
                            }
                            edges { id kind source target }
                        }
                    }
                }
            """
        ).let { data ->
            val map = data.path("branch").path("deliveryMap")
            RenderedMap(
                checkpoints = map.path("checkpoints").toList(),
                edges = map.path("edges").toList(),
            )
        }

}
