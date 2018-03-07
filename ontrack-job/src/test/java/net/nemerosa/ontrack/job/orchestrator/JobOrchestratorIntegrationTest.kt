package net.nemerosa.ontrack.job.orchestrator

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.support.DefaultJobScheduler
import net.nemerosa.ontrack.job.support.TestJob
import net.nemerosa.ontrack.test.assertNotPresent
import net.nemerosa.ontrack.test.assertPresent
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.stream.Stream
import kotlin.test.assertFalse

class JobOrchestratorIntegrationTest {

    private lateinit var scheduledExecutorService: ScheduledExecutorService

    @Before
    fun before() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    }

    @After
    fun after() {
        scheduledExecutorService.shutdownNow()
    }

    @Test
    fun `Removed job`() {
        withOrchestrator {
            // Two jobs
            supplier += "1"
            supplier += "2"
            // Fires the orchestration and checks jobs
            orchestration {
                job("1") {
                    assertFalse(isDisabled)
                }
                job("2") {
                    assertFalse(isDisabled)
                }
            }
            // Removing a job from the supplier
            supplier -= "2"
            // Fires the orchestration and checks jobs
            orchestration {
                job("1") {
                    assertFalse(isDisabled)
                }
                noJob("2")
            }
        }
    }

    @Test
    fun `Added job`() {
        withOrchestrator {
            // Two jobs
            supplier += "1"
            supplier += "2"
            // Fires the orchestration and checks jobs
            orchestration {
                job("1") {
                    assertFalse(isDisabled)
                }
                job("2") {
                    assertFalse(isDisabled)
                }
            }
            // Adding a job to the supplier
            supplier += "3"
            // Fires the orchestration and checks jobs
            orchestration {
                job("1") {
                    assertFalse(isDisabled)
                }
                job("2") {
                    assertFalse(isDisabled)
                }
                job("3") {
                    assertFalse(isDisabled)
                }
            }
        }
    }

    private fun withOrchestrator(code: OrchestratorContext.() -> Unit) {
        // Supplier
        val supplier = TestJobOrchestratorSupplier()
        // OK
        OrchestratorContext(scheduledExecutorService, supplier).code()
    }

    class OrchestratorContext(
            scheduledExecutorService: ScheduledExecutorService,
            val supplier: TestJobOrchestratorSupplier
    ) {
        private val scheduler: JobScheduler
        private val orchestrator: JobOrchestrator

        init {
            scheduler = DefaultJobScheduler(
                    NOPJobDecorator.INSTANCE,
                    scheduledExecutorService,
                    OutputJobListener({ println(it) }),
                    false,
                    false,
                    1.0
            )
            orchestrator = JobOrchestrator(
                    scheduler,
                    "Test",
                    listOf(supplier)
            )
        }

        fun orchestration(code: OrchestrationContext.() -> Unit) {
            // Registration of the orchestrator
            scheduler.schedule(orchestrator, Schedule.NONE)
            // Fires the orchestration
            scheduler.fireImmediately(orchestrator.key).orElseThrow { RuntimeException("Not scheduled") }.get()
            // Code
            OrchestrationContext(scheduler).code()
        }
    }

    class OrchestrationContext(
            private val jobScheduler: JobScheduler
    ) {
        fun job(name: String, code: JobStatus.() -> Unit) {
            val status = jobScheduler.getJobStatus(TestJobOrchestratorSupplier.key(name))
            assertPresent(status, "Job with key $name must be present.") {
                it.code()
            }
        }

        fun noJob(name: String) {
            val status = jobScheduler.getJobStatus(TestJobOrchestratorSupplier.key(name))
            assertNotPresent(status, "Job with key $name must not be present.")
        }
    }

    class TestJobOrchestratorSupplier : JobOrchestratorSupplier {

        private val jobs: MutableMap<String, Job> = mutableMapOf()

        override fun collectJobRegistrations(): Stream<JobRegistration> {
            return jobs.values.map { JobRegistration.of(it).withSchedule(Schedule.EVERY_DAY) }.stream()
        }

        operator fun plusAssign(name: String) {
            jobs[name] = TestJob.of(name)
        }

        operator fun minusAssign(name: String) {
            jobs.remove(name)
        }

        companion object {
            fun key(name: String): JobKey = TestJob.key(name)
        }

    }

}