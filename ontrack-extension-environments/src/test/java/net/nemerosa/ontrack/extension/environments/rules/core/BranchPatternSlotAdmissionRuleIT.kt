package net.nemerosa.ontrack.extension.environments.rules.core

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Build
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BranchPatternSlotAdmissionRuleIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var branchPatternSlotAdmissionRule: BranchPatternSlotAdmissionRule

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @BeforeEach
    fun setup() {
        branchPatternSlotAdmissionRule = BranchPatternSlotAdmissionRule()
    }

    @Test
    fun `Build eligible if included in branch pattern`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch("release-1.31") {
                build {
                    assertTrue(
                        branchPatternSlotAdmissionRule.isBuildEligible(
                            build = this,
                            slot = slot,
                            config = BranchPatternSlotAdmissionRuleConfig(
                                includes = listOf("release-.*"),
                                excludes = emptyList(),
                            )
                        ),
                        "Build is eligible"
                    )
                }
            }
        }
    }

    @Test
    fun `Build eligible if excluded by branch pattern`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch("release-1.31") {
                build {
                    assertFalse(
                        branchPatternSlotAdmissionRule.isBuildEligible(
                            build = this,
                            slot = slot,
                            config = BranchPatternSlotAdmissionRuleConfig(
                                includes = listOf("release-.*"),
                                excludes = listOf("release-1\\.3.*"),
                            )
                        ),
                        "Build is not eligible because excluded"
                    )
                }
            }
        }
    }

    @Test
    fun `List of eligible builds based on branch pattern`() {
        slotTestSupport.withSlot { slot ->
            slotService.addAdmissionRuleConfig(
                config = SlotAdmissionRuleConfig(
                    slot = slot,
                    name = "releaseBranches",
                    description = null,
                    ruleId = BranchPatternSlotAdmissionRule.ID,
                    ruleConfig = BranchPatternSlotAdmissionRuleConfig(
                        includes = listOf("release-.*"),
                        excludes = emptyList()
                    ).asJson()
                ),
            )
            slot.project.apply {
                val expectedBuilds = mutableListOf<Build>()
                branch("master") {
                    build()
                }
                branch("release-1.31") {
                    expectedBuilds += build()
                }
                branch("release-1.32") {
                    expectedBuilds += build()
                }
                val actualBuilds = slotService.getEligibleBuilds(slot)
                assertEquals(
                    expectedBuilds.reversed(),
                    actualBuilds
                )
            }
        }
    }

    @Test
    fun `Deployability is the same as eligibility for the branch pattern, positive test`() {
        slotTestSupport.withSlotPipeline(branchName = "release-1.31") { pipeline ->
            slotService.addAdmissionRuleConfig(
                config = SlotAdmissionRuleConfig(
                    slot = pipeline.slot,
                    name = "releaseBranches",
                    description = null,
                    ruleId = BranchPatternSlotAdmissionRule.ID,
                    ruleConfig = BranchPatternSlotAdmissionRuleConfig(
                        includes = listOf("release-.*"),
                        excludes = emptyList()
                    ).asJson()
                ),
            )
            val check = slotService.runDeployment(
                pipelineId = pipeline.id,
                dryRun = true,
            )
            assertTrue(check.ok, "Build can be deployed")
        }
    }

    @Test
    fun `Deployability is the same as eligibility for the branch pattern, negative test`() {
        slotTestSupport.withSlotPipeline(branchName = "release-1.31") { pipeline ->
            slotService.addAdmissionRuleConfig(
                config = SlotAdmissionRuleConfig(
                    slot = pipeline.slot,
                    name = "releaseBranches",
                    description = null,
                    ruleId = BranchPatternSlotAdmissionRule.ID,
                    ruleConfig = BranchPatternSlotAdmissionRuleConfig(
                        includes = listOf("release-.*"),
                        excludes = listOf("release-1\\..*")
                    ).asJson()
                ),
            )
            val check = slotService.runDeployment(
                pipelineId = pipeline.id,
                dryRun = true,
            )
            assertFalse(check.ok, "Build cannot be deployed")
        }
    }

}