package net.nemerosa.ontrack.extension.dm.tse

import net.nemerosa.ontrack.extension.api.support.TestMetricsExportExtension
import net.nemerosa.ontrack.extension.git.branching.BranchingModelProperty
import net.nemerosa.ontrack.extension.git.branching.BranchingModelPropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.model.support.NameValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

class TimeSinceEventServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var timeSinceEventService: TimeSinceEventService

    @Autowired
    private lateinit var testMetricsExportExtension: TestMetricsExportExtension

    @BeforeEach
    fun enable() {
        testMetricsExportExtension.enable()
    }

    @AfterEach
    fun disable() {
        testMetricsExportExtension.disable()
    }

    @Test
    fun `Relative time to promotion when there is a previous promotion level`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val silver = promotionLevel("SILVER")
                val gold = promotionLevel("GOLD")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    promote(silver, time = 80.hours.ago)
                    promote(gold, time = 70.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    // Not promoted
                }
                build("3") {
                    updateBuildSignature(signature.user.name, 30.hours.ago)
                    promote(silver, time = 25.hours.ago)
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_relative_time_since_promotion",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "promotion" to "SILVER"
                ),
                fields = mapOf(
                    "hours" to 0.0, // Time since SILVER(3)
                    "days" to 0.0
                ),
                timestamp = null
            )
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_relative_time_since_promotion",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "promotion" to "GOLD"
                ),
                fields = mapOf(
                    "hours" to 25.0, // Time since SILVER(3)
                    "days" to 1.0
                ),
                timestamp = null
            )
        }
    }

    @Test
    fun `Relative time to promotion when there is no previous promotion level`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val silver = promotionLevel("SILVER")
                val gold = promotionLevel("GOLD")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    promote(silver, time = 80.hours.ago)
                    promote(gold, time = 70.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    // Not promoted
                }
                build("3") {
                    updateBuildSignature(signature.user.name, 30.hours.ago)
                    promote(silver, time = 25.hours.ago)
                    promote(gold, time = 24.hours.ago)
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_relative_time_since_promotion",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "promotion" to "SILVER"
                ),
                fields = mapOf(
                    "hours" to 0.0, // Time since SILVER(3)
                    "days" to 0.0
                ),
                timestamp = null
            )
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_relative_time_since_promotion",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "promotion" to "GOLD"
                ),
                fields = mapOf(
                    "hours" to 0.0, // Time since SILVER(3)
                    "days" to 0.0
                ),
                timestamp = null
            )
        }
    }

    @Test
    fun `Relative time to first promotion when there is more recent build`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val silver = promotionLevel("SILVER")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    promote(silver, time = 80.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    // Not promoted, but "starting the counter"
                }
                build("3") {
                    updateBuildSignature(signature.user.name, 30.hours.ago)
                    // Not promoted
                }
                build("4") {
                    updateBuildSignature(signature.user.name, 20.hours.ago)
                    // Not promoted
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_relative_time_since_promotion",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "promotion" to "SILVER"
                ),
                fields = mapOf(
                    "hours" to 40.0, // Time since BUILD(2)
                    "days" to 1.0
                ),
                timestamp = null
            )
        }
    }

    @Test
    fun `Relative time to first promotion when there is no more recent build`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val silver = promotionLevel("SILVER")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    promote(silver, time = 80.hours.ago)
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_relative_time_since_promotion",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "promotion" to "SILVER"
                ),
                fields = mapOf(
                    "hours" to 0.0, // Time since BUILD(1)
                    "days" to 0.0
                ),
                timestamp = null
            )
        }
    }

    @Test
    fun `Metric enabled for main branch in project with branch model`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val iron = promotionLevel("IRON")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    promote(iron, time = 80.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    // Not promoted
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_promotion",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "promotion" to "IRON"
                ),
                fields = mapOf(
                    "hours" to 80.0,
                    "days" to 3.0
                ),
                timestamp = null
            )
        }
    }

    @Test
    fun `Metric set to 0 when last build is promoted`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val iron = promotionLevel("IRON")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    promote(iron, time = 80.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    promote(iron, time = 32.hours.ago)
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_promotion",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "promotion" to "IRON"
                ),
                fields = mapOf(
                    "hours" to 0.0,
                    "days" to 0.0
                ),
                timestamp = null
            )
        }
    }

    @Test
    fun `Metric not enabled for branch not in project branch model`() {
        project {
            setSampleBranchModel()
            branch("feature-awesome") {
                val iron = promotionLevel("IRON")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    promote(iron, time = 80.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    // Not promoted
                }
            }
            // Clears previous metrics
            testMetricsExportExtension.clear()
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertNoMetric("ontrack_dm_time_since_promotion")
        }
    }

    @Test
    fun `Metric not enabled when no promotion run`() {
        project {
            setSampleBranchModel()
            branch("main") {
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                }
            }
            // Clears previous metrics
            testMetricsExportExtension.clear()
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertNoMetric("ontrack_dm_time_since_promotion")
        }
    }

    @Test
    fun `Metric not enabled when no build`() {
        project {
            setBranchModel(
                mapOf(
                    "release" to "release-.*",
                    "maintenance" to "maintenance-.*",
                    "development" to "main"
                )
            )
            branch("main")
            // Clears previous metrics
            testMetricsExportExtension.clear()
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertNoMetric("ontrack_dm_time_since_promotion")
        }
    }

    @Test
    fun `Metric not enabled for disabled branch`() {
        project {
            setSampleBranchModel()
            branch("main") {
                disableBranch()
                val iron = promotionLevel("IRON")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    promote(iron, time = 80.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    // Not promoted
                }
            }
            // Clears previous metrics
            testMetricsExportExtension.clear()
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertNoMetric("ontrack_dm_time_since_promotion")
        }
    }

    @Test
    fun `Validation metric enabled for main branch in project with branch model`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val vs = validationStamp("VS")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    validateWithTime(vs, ValidationRunStatusID.STATUS_PASSED, time = 80.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    // Not validated at all
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_passed_validation",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "validation_stamp" to "VS"
                ),
                fields = mapOf(
                    "hours" to 80.0,
                    "days" to 3.0
                ),
                timestamp = null
            )
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_validation",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "validation_stamp" to "VS",
                    "status" to "PASSED"
                ),
                fields = mapOf(
                    "hours" to 80.0,
                    "days" to 3.0
                ),
                timestamp = null
            )
        }
    }

    @Test
    fun `Validation metric for last build being PASSED`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val vs = validationStamp("VS")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    validateWithTime(vs, ValidationRunStatusID.STATUS_PASSED, time = 80.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    validateWithTime(vs, ValidationRunStatusID.STATUS_PASSED, time = 32.hours.ago)
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_passed_validation",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "validation_stamp" to "VS"
                ),
                fields = mapOf(
                    "hours" to 0.0,
                    "days" to 0.0
                ),
                timestamp = null
            )
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_validation",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "validation_stamp" to "VS",
                    "status" to "PASSED"
                ),
                fields = mapOf(
                    "hours" to 0.0,
                    "days" to 0.0
                ),
                timestamp = null
            )
        }
    }

    @Test
    fun `Validation metric for last build being FAILED`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val vs = validationStamp("VS")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    validateWithTime(vs, ValidationRunStatusID.STATUS_PASSED, time = 80.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    validateWithTime(vs, ValidationRunStatusID.STATUS_FAILED, time = 32.hours.ago)
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_passed_validation",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "validation_stamp" to "VS"
                ),
                fields = mapOf(
                    "hours" to 80.0,
                    "days" to 3.0
                ),
                timestamp = null
            )
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_validation",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "validation_stamp" to "VS",
                    "status" to "FAILED"
                ),
                fields = mapOf(
                    "hours" to 0.0,
                    "days" to 0.0
                ),
                timestamp = null
            )
        }
    }

    private fun Project.setSampleBranchModel() {
        setBranchModel(
            mapOf(
                "release" to "release-.*",
                "maintenance" to "maintenance-.*",
                "development" to "main"
            )
        )
    }

    @Test
    fun `Validation metric for previous build being FAILED`() {
        project {
            setSampleBranchModel()
            branch("main") {
                val vs = validationStamp("VS")
                build("1") {
                    updateBuildSignature(signature.user.name, 82.hours.ago)
                    validateWithTime(vs, ValidationRunStatusID.STATUS_PASSED, time = 80.hours.ago)
                }
                build("2") {
                    updateBuildSignature(signature.user.name, 40.hours.ago)
                    validateWithTime(vs, ValidationRunStatusID.STATUS_FAILED, time = 32.hours.ago)
                }
                build("3") {
                    updateBuildSignature(signature.user.name, 26.hours.ago)
                }
            }
            // Exports the metrics
            timeSinceEventService.collectTimesSinceEvents(project) { println(it) }
            // Checks the metrics have been exported
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_passed_validation",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "validation_stamp" to "VS"
                ),
                fields = mapOf(
                    "hours" to 80.0,
                    "days" to 3.0
                ),
                timestamp = null
            )
            testMetricsExportExtension.assertHasMetric(
                metric = "ontrack_dm_time_since_validation",
                tags = mapOf(
                    "project" to name,
                    "branch" to "main",
                    "validation_stamp" to "VS",
                    "status" to "FAILED"
                ),
                fields = mapOf(
                    "hours" to 32.0,
                    "days" to 1.0
                ),
                timestamp = null
            )
        }
    }

    /**
     * Setting a branch model for a project
     */
    private fun Project.setBranchModel(model: Map<String, String>?) {
        if (model != null) {
            setProperty(
                this,
                BranchingModelPropertyType::class.java,
                BranchingModelProperty(
                    patterns = model.map {
                        NameValue(it.key, it.value)
                    }
                )
            )
        } else {
            deleteProperty(
                this,
                BranchingModelPropertyType::class.java,
            )
        }
    }

    /**
     * Disabling a branch
     */
    fun Branch.disableBranch() {
        structureService.disableBranch(this)
    }

    /**
     * Converts a number of hours into a [Duration].
     */
    val Int.hours: Duration get() = Duration.ofHours(toLong())

    /**
     * Given a [Duration], substracts it to the current time (UTC)
     * and returns the new time.
     */
    val Duration.ago: LocalDateTime
        get() =
            LocalDateTime.now(ZoneOffset.UTC).minus(this)

}