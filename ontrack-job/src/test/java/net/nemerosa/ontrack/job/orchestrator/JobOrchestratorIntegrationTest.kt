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

    private fun createJobScheduler(): JobScheduler {
        return DefaultJobScheduler(
                NOPJobDecorator.INSTANCE,
                scheduledExecutorService,
                OutputJobListener({ println(it) }),
                false,
                false,
                1.0
        )
    }

    @Test
    fun `Removed job`() {
        // Job scheduler
        val scheduler = createJobScheduler()
        // Supplier
        val supplier = TestJobOrchestratorSupplier()
        // Two jobs
        supplier += "1"
        supplier += "2"
        // Orchestrator
        val orchestrator = JobOrchestrator(
                scheduler,
                "Test",
                listOf(supplier)
        )
        val orchestratorKey = orchestrator.key
        // Registration of the orchestrator
        scheduler.schedule(orchestrator, Schedule.NONE)
        // Fires the orchestration
        scheduler.fireImmediately(orchestratorKey).orElseThrow { RuntimeException("Not scheduled") }.get()
        // Checks that both jobs are registered and enabled
        val o1 = scheduler.getJobStatus(TestJobOrchestratorSupplier.key("1"))
        assertPresent(o1) {
            assertFalse(it.isDisabled)
        }
        val o2 = scheduler.getJobStatus(TestJobOrchestratorSupplier.key("2"))
        assertPresent(o2) {
            assertFalse(it.isDisabled)
        }
        // Removing a job from the supplier
        supplier -= "2"
        // Registration of the orchestrator
        scheduler.schedule(orchestrator, Schedule.NONE)
        // Fires the orchestration
        scheduler.fireImmediately(orchestratorKey).orElseThrow { RuntimeException("Not scheduled") }.get()
        // Checks the jobs
        assertPresent(scheduler.getJobStatus(TestJobOrchestratorSupplier.key("1"))) {
            assertFalse(it.isDisabled)
        }
        assertNotPresent(scheduler.getJobStatus(TestJobOrchestratorSupplier.key("2")))
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