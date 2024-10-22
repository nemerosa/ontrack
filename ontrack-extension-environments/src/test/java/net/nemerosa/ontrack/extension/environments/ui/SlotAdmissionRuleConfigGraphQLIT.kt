package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SlotAdmissionRuleConfigGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Getting the list of admission rules`() {
        run(
            """
            {
                slotAdmissionRules {
                    id
                    name
                }
            }
        """.trimIndent()
        ) { data ->
            val rules = data.path("slotAdmissionRules")
            val rule = rules.find { it.path("id").asText() == "promotion" }
            assertNotNull(rule, "Promotion rule found") {
                assertEquals("Promotion", it.path("name").asText())
            }
        }
    }

    @Test
    fun `Getting the list of admission rules for a slot`() {
        slotTestSupport.withSlot { slot ->
            val config = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(slot)
            slotService.addAdmissionRuleConfig(
                slot = slot,
                config = config,
            )
            run(
                """
                    {
                        slotById(id: "${slot.id}") {
                            admissionRules {
                                id
                                name
                                description
                                ruleId
                                ruleConfig
                            }
                        }
                    }
                """
            ) { data ->
                val rules = data.path("slotById").path("admissionRules")
                assertEquals(1, rules.size())
                val rule = rules.first()
                assertEquals(config.id, rule.path("id").asText())
                assertEquals(config.name, rule.path("name").asText())
                assertEquals(config.ruleId, rule.path("ruleId").asText())
                assertEquals(config.ruleConfig, rule.path("ruleConfig"))
            }
        }
    }

    @Test
    fun `Creating a new admission rule`() {
        slotTestSupport.withSlot { slot ->
            val name = uid("rule-")
            run(
                """
                    mutation {
                        saveSlotAdmissionRuleConfig(input: {
                            slotId: "${slot.id}",
                            name: "$name",
                            description: "Rule $name",
                            ruleId: "promotion",
                            ruleConfig: {
                                promotion: "GOLD"
                            },
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """.trimIndent()
            ) { data ->
                checkGraphQLUserErrors(data, "saveSlotAdmissionRuleConfig")
                val rules = slotService.getAdmissionRuleConfigs(slot)
                val rule = rules.find { it.name == name }
                assertNotNull(rule, "Rule found") {
                    assertEquals("Rule $name", it.description)
                    assertEquals("promotion", it.ruleId)
                    assertEquals("GOLD", it.ruleConfig.path("promotion").asText())
                }
            }
        }
    }

//    @Test
//    fun `TODO Saving changes on an existing admission rule`() {
//        slotTestSupport.withSlot { slot ->
//            val config = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(slot)
//            slotService.addAdmissionRuleConfig(
//                slot = slot,
//                config = config,
//            )
//            run(
//                """
//                    mutation {
//                        saveSlotAdmissionRuleConfig(input: {
//                            id: "${config.id}",
//                            name: "${config.name}",
//                            description: "${config.description}",
//                            ruleId: "promotion",
//                            ruleConfig: {
//                                promotion: "SILVER"
//                            },
//                        }) {
//                            errors {
//                                message
//                            }
//                        }
//                    }
//                """.trimIndent()
//            ) { data ->
//                checkGraphQLUserErrors(data, "saveSlotAdmissionRuleConfig")
//                val rules = slotService.getAdmissionRuleConfigs(slot)
//                val rule = rules.find { it.id == config.id }
//                assertNotNull(rule, "Rule found") {
//                    assertEquals(config.name, it.name)
//                    assertEquals(config.description, it.description)
//                    assertEquals("promotion", it.ruleId)
//                    assertEquals("SILVER", it.ruleConfig.path("promotion").asText())
//                }
//            }
//        }
//    }

}