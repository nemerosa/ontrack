package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.rules.core.ManualApprovalSlotAdmissionRule
import net.nemerosa.ontrack.extensions.environments.rules.core.ManualApprovalSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extensions.environments.rules.core.ManualApprovalSlotAdmissionRuleData
import net.nemerosa.ontrack.extensions.environments.rules.core.ManualApprovalSlotAdmissionRuleException
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.extensions.environments.service.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class ManualApprovalIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Approval by a super admin`() {
        withManuallyApprovedPipeline { pipeline, admissionRuleConfig ->
            asAdmin {
                slotService.approve(pipeline, admissionRuleConfig)
                val status = slotService.startDeployment(
                    pipeline,
                    dryRun = true
                )
                assertTrue(status.status, "Deployment accepted")
                val check = status.checks.first()
                assertTrue(check.check.status, "Manual approval OK")
                assertNull(check.override, "Manual approval not overridden")

                val state =
                    slotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(pipeline, admissionRuleConfig.id)
                assertNotNull(state, "Manual approval state stored") {
                    assertEquals("admin", it.user)
                    assertEquals(
                        ManualApprovalSlotAdmissionRuleData(
                            approval = true,
                            message = "With pleasure"
                        ),
                        it.data?.parse()
                    )
                }
            }
        }
    }

    @Test
    fun `Manual approval rejected by a super admin`() {
        withManuallyApprovedPipeline { pipeline, admissionRuleConfig ->
            asAdmin {
                slotService.approve(
                    pipeline,
                    admissionRuleConfig,
                    approval = false,
                    message = "No way"
                )
                val status = slotService.startDeployment(
                    pipeline,
                    dryRun = true
                )
                assertFalse(status.status, "Deployment rejected")
                val check = status.checks.first()
                assertFalse(check.check.status, "Manual approval rejected")
                assertNull(check.override, "Manual approval not overridden")

                val state =
                    slotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(pipeline, admissionRuleConfig.id)
                assertNotNull(state, "Manual approval state stored") {
                    assertEquals("admin", it.user)
                    assertEquals(
                        ManualApprovalSlotAdmissionRuleData(
                            approval = false,
                            message = "No way"
                        ),
                        it.data?.parse()
                    )
                }
            }
        }
    }

    private fun SlotService.approve(
        pipeline: SlotPipeline,
        admissionRuleConfig: SlotAdmissionRuleConfig,
        approval: Boolean = true,
        message: String = "With pleasure",
    ) {
        setupAdmissionRule(
            pipeline,
            admissionRuleConfig,
            ManualApprovalSlotAdmissionRuleData(
                approval = approval,
                message = message
            ).asJson()
        )
    }

    private fun withManuallyApprovedPipeline(
        users: List<String> = emptyList(),
        code: (pipeline: SlotPipeline, admissionRuleConfig: SlotAdmissionRuleConfig) -> Unit,
    ) {
        slotTestSupport.withSlotPipeline { pipeline ->
            val ruleConfig = ManualApprovalSlotAdmissionRuleConfig(
                message = "Approval required",
                users = users,
            )
            val admissionRuleConfig = SlotAdmissionRuleConfig(
                slot = pipeline.slot,
                name = "Manual approval",
                description = "Manual approval is required",
                ruleId = ManualApprovalSlotAdmissionRule.ID,
                ruleConfig = ruleConfig.asJson()
            )
            slotService.addAdmissionRuleConfig(
                pipeline.slot,
                admissionRuleConfig
            )

            code(pipeline, admissionRuleConfig)
        }
    }

    @Test
    fun `Manual rejected approval overridden by an admin`() {
        withManuallyApprovedPipeline { pipeline, admissionRuleConfig ->
            asAdmin {
                slotService.approve(
                    pipeline,
                    admissionRuleConfig,
                    approval = false,
                    message = "No way"
                )

                // Overriding the decision
                slotService.overrideAdmissionRule(
                    pipeline = pipeline,
                    admissionRuleConfig = admissionRuleConfig,
                    message = "Because I want to",
                )

                val status = slotService.startDeployment(
                    pipeline,
                    dryRun = true
                )
                assertTrue(status.status, "Deployment accepted")
                val check = status.checks.first()
                assertFalse(check.check.status, "Manual approval rejected")
                assertNotNull(check.override, "Manual rejection overridden") {
                    assertEquals("admin", it.user)
                    assertTrue(it.override, "Overridden")
                    assertEquals("Because I want to", it.overrideMessage)
                }

                val state =
                    slotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(pipeline, admissionRuleConfig.id)
                assertNotNull(state, "Manual approval state stored") {
                    assertEquals("admin", it.user)
                    assertEquals(
                        ManualApprovalSlotAdmissionRuleData(
                            approval = false,
                            message = "No way"
                        ),
                        it.data?.parse()
                    )
                }
            }
        }
    }

    @Test
    fun `Manual approval by anybody`() {
        withManuallyApprovedPipeline { pipeline, admissionRuleConfig ->
            slotTestSupport.withSlotUser(pipeline.slot) {
                slotService.approve(pipeline, admissionRuleConfig)
            }
            val status = asAdmin {
                slotService.startDeployment(
                    pipeline,
                    dryRun = true
                )
            }
            assertTrue(status.status, "Deployment accepted")
        }
    }

    @Test
    fun `Manual approval by an allowed user`() {
        val name = uid("U")
        withManuallyApprovedPipeline(
            users = listOf(name),
        ) { pipeline, admissionRuleConfig ->
            slotTestSupport.withSlotUser(
                name = name,
                slot = pipeline.slot,
            ) {
                slotService.approve(pipeline, admissionRuleConfig)
            }
            val status = slotService.startDeployment(
                pipeline,
                dryRun = true
            )
            assertTrue(status.status, "Deployment accepted")
            val state =
                slotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(
                    pipeline,
                    admissionRuleConfig.id
                )
            assertNotNull(state, "Manual approval state stored") {
                assertEquals(name, it.user)
            }
        }
    }

    @Test
    fun `Manual approval not possible if not in the list of users`() {
        val name = uid("U")
        val otherName = uid("U")
        withManuallyApprovedPipeline(
            users = listOf(name),
        ) { pipeline, admissionRuleConfig ->
            slotTestSupport.withSlotUser(name = otherName, slot = pipeline.slot) {
                assertFailsWith<ManualApprovalSlotAdmissionRuleException> {
                    slotService.approve(pipeline, admissionRuleConfig)
                }
            }
        }
    }
}