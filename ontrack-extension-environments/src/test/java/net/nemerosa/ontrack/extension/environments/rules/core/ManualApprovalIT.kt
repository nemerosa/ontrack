package net.nemerosa.ontrack.extension.environments.rules.core

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.service.getPipelineAdmissionRuleChecks
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
                val status = slotService.runDeployment(
                    pipeline.id,
                    dryRun = true
                )
                assertTrue(status.ok, "Deployment accepted")
                val check = slotService.getPipelineAdmissionRuleChecks(pipeline).first()
                assertTrue(check.ok, "Manual approval OK")

                val state =
                    slotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(pipeline, admissionRuleConfig.id)
                assertNotNull(state, "Manual approval state stored") {
                    assertEquals(securityService.currentUser?.name, it.data?.user)
                    assertEquals(
                        ManualApprovalSlotAdmissionRuleData(
                            approval = true,
                            message = "With pleasure"
                        ),
                        it.data?.data?.parse()
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
                val status = slotService.runDeployment(
                    pipeline.id,
                    dryRun = true
                )
                assertFalse(status.ok, "Deployment rejected")
                val check = slotService.getPipelineAdmissionRuleChecks(pipeline).first()
                assertFalse(check.ok, "Manual approval rejected")

                val state =
                    slotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(pipeline, admissionRuleConfig.id)
                assertNotNull(state, "Manual approval state stored") {
                    assertEquals(securityService.currentUser?.name, it.data?.user)
                    assertEquals(
                        ManualApprovalSlotAdmissionRuleData(
                            approval = false,
                            message = "No way"
                        ),
                        it.data?.data?.parse()
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
                name = "manualApproval",
                description = "Manual approval is required",
                ruleId = ManualApprovalSlotAdmissionRule.ID,
                ruleConfig = ruleConfig.asJson()
            )
            slotService.addAdmissionRuleConfig(
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

                val status = slotService.runDeployment(
                    pipeline.id,
                    dryRun = true
                )
                assertTrue(status.ok, "Deployment accepted")
                val check = slotService.getPipelineAdmissionRuleChecks(pipeline).first()
                assertTrue(check.ok, "Manual approval rejected but overridden")
                val ruleStatus = slotService.getPipelineAdmissionRuleStatuses(pipeline).first()
                assertNotNull(ruleStatus.override, "Manual rejection overridden") {
                    assertEquals(securityService.currentUser?.name, it.user)
                    assertEquals("Because I want to", it.message)
                }

                val state =
                    slotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(pipeline, admissionRuleConfig.id)
                assertNotNull(state, "Manual approval state stored") {
                    assertEquals(securityService.currentUser?.name, it.data?.user)
                    assertEquals(
                        ManualApprovalSlotAdmissionRuleData(
                            approval = false,
                            message = "No way"
                        ),
                        it.data?.data?.parse()
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
                slotService.runDeployment(
                    pipeline.id,
                    dryRun = true
                )
            }
            assertTrue(status.ok, "Deployment accepted")
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
            val status = slotService.runDeployment(
                pipeline.id,
                dryRun = true
            )
            assertTrue(status.ok, "Deployment accepted")
            val state =
                slotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(
                    pipeline,
                    admissionRuleConfig.id
                )
            assertNotNull(state, "Manual approval state stored") {
                assertEquals(name, it.data?.user)
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