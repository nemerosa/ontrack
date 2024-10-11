package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class SlotAdmissionRuleIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Adding an admission rule to a slot`() {
        slotTestSupport.withSlot { slot ->
            val config = SlotAdmissionRuleTestFixtures.testAdmissionRuleConfig(slot)
            slotService.addAdmissionRuleConfig(slot, config)
        }
    }

    @Test
    fun `Getting the admission rules for a slot`() {
        slotTestSupport.withSlot { slot ->
            val config1 = SlotAdmissionRuleTestFixtures.testAdmissionRuleConfig(slot).apply {
                slotService.addAdmissionRuleConfig(slot, this)
            }
            val config2 = SlotAdmissionRuleTestFixtures.testAdmissionRuleConfig(slot).apply {
                slotService.addAdmissionRuleConfig(slot, this)
            }
            val configs = slotService.getAdmissionRuleConfigs(slot)
            assertEquals(
                listOf(config1.name, config2.name).sorted(),
                configs.map { it.name }
            )
        }
    }

    @Test
    fun `Deleting an admission rule from a slot`() {
        slotTestSupport.withSlot { slot ->
            val config = SlotAdmissionRuleTestFixtures.testAdmissionRuleConfig(slot).apply {
                slotService.addAdmissionRuleConfig(slot, this)
            }
            assertNotNull(
                slotService.getAdmissionRuleConfigs(slot).find { it.name == config.name },
                "Config found"
            )
            slotService.deleteAdmissionRuleConfig(config)
            assertNull(
                slotService.getAdmissionRuleConfigs(slot).find { it.name == config.name },
                "Config gone"
            )
        }
    }

    @Test
    fun `Checking if a build is eligible to a slot`() {
        slotTestSupport.withSlot { slot ->
            SlotAdmissionRuleTestFixtures.testAdmissionRuleConfig(slot).apply {
                slotService.addAdmissionRuleConfig(slot, this)
            }

            // Creating a branch
            slot.project.branch {

                // No promotion, just a build
                build {
                    assertFalse(
                        slotService.isBuildEligible(slot, this),
                        "Build not eligible because no promotion"
                    )
                }

                // Promotion, build not promoted
                val pl = promotionLevel("GOLD")
                build {
                    assertTrue(
                        slotService.isBuildEligible(slot, this),
                        "Build not eligible because not promoted"
                    )
                }

                // Build promoted
                build {
                    promote(pl)
                    assertTrue(
                        slotService.isBuildEligible(slot, this),
                        "Build eligible because promoted"
                    )
                }

            }
        }
    }

    @Test
    fun `Always checking that the build is in the same project as the slot`() {
        slotTestSupport.withSlot { slot ->
            SlotAdmissionRuleTestFixtures.testAdmissionRuleConfig(slot).apply {
                slotService.addAdmissionRuleConfig(slot, this)
            }

            // Creating another project & branch
            project {
                branch {
                    val pl = promotionLevel("GOLD")
                    build {
                        assertFalse(
                            slotService.isBuildEligible(slot, this),
                            "Build OK but not in the correct project"
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Get the eligible builds for a slot`() {
        slotTestSupport.withSlot { slot ->
            SlotAdmissionRuleTestFixtures.testAdmissionRuleConfig(slot).apply {
                slotService.addAdmissionRuleConfig(slot, this)
            }

            slot.project.branch {
                val pl = promotionLevel("GOLD")
                val build10 = build()
                val build11 = build {
                    promote(pl)
                }
                val build12 = build()

                slot.project.branch {
                    /* val build20 = */ build()
                    /* val build21 = */ build()

                    // Only the builds of the branches containing the GOLD promotion are eligible
                    val builds = slotService.getEligibleBuilds(slot)
                    assertEquals(
                        listOf(
                            build12,
                            build11,
                            build10
                        ),
                        builds
                    )
                }
            }
        }
    }

}