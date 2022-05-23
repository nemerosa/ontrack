package net.nemerosa.ontrack.extension.dm.export

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.api.support.TestMetricsExportExtension
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Project
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
                        testExportMetrics(now, ref = project)
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

    private fun testExportMetrics(
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