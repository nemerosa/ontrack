package net.nemerosa.ontrack.extension.environments.rules.core

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PromotionSlotAdmissionRuleIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotService: SlotService

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Test
    fun `Getting list of eligible builds for a slot`() {
        asAdmin {
            slotTestSupport.withSlot { slot ->

                val main = slot.project.branch(name = "main")
                val silver = main.promotionLevel("SILVER")

                val otherBranch = slot.project.branch(name = "other")

                val build1 = main.build {
                    promote(silver)
                }
                val build2 = main.build()
                /* val build3 = */ otherBranch.build()

                slotService.addAdmissionRuleConfig(
                    SlotAdmissionRuleConfig(
                        slot = slot,
                        name = "Promotion",
                        description = null,
                        ruleId = PromotionSlotAdmissionRule.ID,
                        ruleConfig = PromotionSlotAdmissionRuleConfig(promotion = silver.name).asJson(),
                    )
                )

                val eligibleBuilds = slotService.getEligibleBuilds(slot = slot).pageItems
                assertEquals(
                    listOf(build2.id, build1.id),
                    eligibleBuilds.map { it.id }
                )

                val deployableBuilds = slotService.getEligibleBuilds(slot = slot, deployable = true).pageItems
                assertEquals(
                    listOf(build1.id),
                    deployableBuilds.map { it.id }
                )
            }
        }
    }

}