package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.api.support.TestMetricsExportExtension
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.LocalDateTime

class PromotionLevelMetricsExportIT : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var endToEndPromotionMetricsExportService: EndToEndPromotionMetricsExportService

    @Autowired
    protected lateinit var testMetricsExportExtension: TestMetricsExportExtension

    @BeforeEach
    fun before() {
        testMetricsExportExtension.clear()
        testMetricsExportExtension.enable()
    }

    @AfterEach
    fun after() {
        testMetricsExportExtension.disable()
    }

    @Test
    fun `Single project lead time`() {
        val now = Time.now()
        project {
            branch("main") {
                val pl = promotionLevel()
                build {
                    updateBuildSignature(time = now.minusHours(10))
                    promote(pl, time = now.minusHours(6))
                    testMetricsExportExtension.with {
                        exportMetrics(now, ref = project)
                    }
                    testMetricsExportExtension.assertHasMetric(
                        metric = "ontrack_dm_promotion_lead_time",
                        tags = mapOf(
                            "targetProject" to project.name,
                            "targetBranch" to branch.name,
                            "sourceProject" to project.name,
                            "sourceBranch" to branch.name,
                            "promotion" to pl.name,
                        ),
                        fields = mapOf(
                            "value" to Duration.ofHours(4).toSeconds().toDouble()
                        ),
                        timestamp = null
                    )
                }
            }
        }
    }

    @Test
    fun `End to end time to promotion must take into account the oldest reference, not the latest one`() {
        asAdmin {
            val ref = Time.now()
            val promotion = TestUtils.uid("P")
            // Source project
            val sourceName = TestUtils.uid("source-")
            val source = project(NameDescription.nd(sourceName, ""))
            val sourceBranch = source.branch("main")
            val sourcePromotionLevel = sourceBranch.run {
                promotionLevel(name = promotion)
            }
            val sourceBuild =
                sourceBranch.build("source-1").updateBuildSignature(time = ref.minusHours(10)) // We count from here
            // Target project
            val targetName = TestUtils.uid("target-")
            val target = project(NameDescription.nd(targetName, ""))
            val targetBranch = target.branch("main")
            val targetPromotionLevel = targetBranch.run {
                promotionLevel(name = promotion) // Alignment on the name
            }
            val targetBuildOldest = targetBranch.build("target-1").updateBuildSignature(time = ref.minusHours(8))
            val targetBuildNewest = targetBranch.build("target-2").updateBuildSignature(time = ref.minusHours(6))
            // Project dependencies as a link
            testMetricsExportExtension.clear()
            // Promotes the source first
            sourceBuild.promote(sourcePromotionLevel, time = ref.minusHours(9))
            // Oldest target project linked first and promoted
            targetBuildOldest.linkTo(sourceBuild)
            targetBuildOldest.promote(targetPromotionLevel, time = ref.minusHours(7)) // We count until here
            // Newest target project linked second and promoted
            targetBuildNewest.linkTo(sourceBuild)
            targetBuildNewest.promote(
                targetPromotionLevel,
                time = ref.minusHours(5)
            ) // We don't care about this dependency
            // Collecting metrics
            exportMetrics(ref, source)
            // First target is registered, that's normal
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_promotion_lead_time",
                tags = mapOf(
                    "targetProject" to targetName,
                    "sourceProject" to sourceName,
                    "targetBranch" to targetBranch.name,
                    "sourceBranch" to sourceBranch.name,
                    "promotion" to promotion
                ),
                fields = mapOf(
                    "value" to Duration.ofHours(3).toSeconds().toDouble()
                ),
                timestamp = null
            )
            // Second target must not be registered
            testMetricsExportExtension.assertNoMetric(
                metric = "ontrack_dm_promotion_lead_time",
                tags = mapOf(
                    "targetProject" to targetName,
                    "sourceProject" to sourceName,
                    "targetBranch" to targetBranch.name,
                    "sourceBranch" to sourceBranch.name,
                    "promotion" to promotion,
                    "targetBuild" to targetBuildNewest.name,
                    "sourceBuild" to sourceBuild.name
                )
            )
        }
    }

    @Test
    fun `End to end time to promotion with dependency promotion earlier than target promotion`() {
        asAdmin {
            val ref = Time.now()
            val promotion = TestUtils.uid("P")
            // Source project
            val sourceName = TestUtils.uid("source-")
            val source = project(NameDescription.nd(sourceName, ""))
            val sourceBranch = source.branch("main")
            val sourcePromotionLevel = sourceBranch.run {
                promotionLevel(name = promotion)
            }
            val sourceBuild =
                sourceBranch.build("source-1").updateBuildSignature(time = ref.minusHours(10)) // We count from here
            // Target project
            val targetName = TestUtils.uid("target-")
            val target = project(NameDescription.nd(targetName, ""))
            val targetBranch = target.branch("main")
            val targetPromotionLevel = targetBranch.run {
                promotionLevel(name = promotion) // Alignment on the name
            }
            val targetBuild = targetBranch.build("target-1").updateBuildSignature(time = ref.minusHours(9))
            // Project dependencies as a link
            // Target project linked to the previous one
            targetBuild.linkTo(sourceBuild)
            // Promotes the source first
            testMetricsExportExtension.clear()
            sourceBuild.promote(sourcePromotionLevel, time = ref.minusHours(8))
            exportMetrics(ref, ref = source)
            // We don't expect anything because the target promotion has not been reached yet
            testMetricsExportExtension.assertNoMetric(
                "ontrack_dm_promotion_lead_time",
                tags = mapOf(
                    "targetProject" to targetName,
                )
            )
            // Promotes the target second
            testMetricsExportExtension.clear()
            targetBuild.promote(targetPromotionLevel, time = ref.minusHours(6)) // We count until here
            exportMetrics(ref, ref = source)
            // Now, promotion has been reached for both
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_promotion_lead_time",
                tags = mapOf(
                    "targetProject" to targetName,
                    "sourceProject" to sourceName,
                    "targetBranch" to targetBranch.name,
                    "sourceBranch" to sourceBranch.name,
                    "promotion" to promotion
                ),
                fields = mapOf(
                    "value" to Duration.ofHours(4).toSeconds().toDouble()
                ),
                timestamp = null
            )
        }
    }

    private fun exportMetrics(
        now: LocalDateTime,
        ref: Project? = null,
    ) {
        endToEndPromotionMetricsExportService.exportMetrics(
            branches = EndToEndPromotionMetricsExportSettings.DEFAULT_BRANCHES,
            start = now.minusDays(EndToEndPromotionMetricsExportSettings.DEFAULT_PAST_DAYS.toLong()),
            end = now,
            refProject = ref?.name,
        )
    }

}