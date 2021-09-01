package net.nemerosa.ontrack.job.orchestrator

import net.nemerosa.ontrack.job.*
import java.util.*
import kotlin.streams.toList
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

class JobOrchestrator(
        private val jobScheduler: JobScheduler,
        private val name: String,
        private val jobOrchestratorSuppliers: Collection<JobOrchestratorSupplier>,
        transactionManager: PlatformTransactionManager,
) : Job {

    private val cache = HashSet<JobKey>()
    private val transactionTemplate = TransactionTemplate(transactionManager)

    override fun getKey(): JobKey {
        return JobCategory.CORE.getType("orchestrator").withName("Orchestrator").getKey(name)
    }

    override fun getTask(): JobRun {
        return JobRun { this.orchestrate(it) }
    }

    @Synchronized
    fun orchestrate(runListener: JobRunListener) {
        // Complete list of registrations
        val registrations = transactionTemplate.execute {
            jobOrchestratorSuppliers.flatMap { it.collectJobRegistrations().toList() }
        } ?: return
        // List of registration keys
        val keys = registrations
                .map { registration -> registration.job.key }
        // Jobs to unschedule
        val toRemove = HashSet(cache)
        toRemove.removeAll(keys)
        toRemove.forEach { jobScheduler.unschedule(it) }
        // Jobs to add / update
        val toRegister = HashSet(keys)
        toRegister.removeAll(toRemove)
        registrations
                .filter { jobRegistration -> toRegister.contains(jobRegistration.job.key) }
                .forEach { jobRegistration -> schedule(jobRegistration, runListener) }
        // Resets the cache
        cache.clear()
        cache.addAll(keys)
    }

    private fun schedule(jobRegistration: JobRegistration, runListener: JobRunListener) {
        runListener.message("Scheduling: %s", jobRegistration.job.key)
        jobScheduler.schedule(jobRegistration.job, jobRegistration.schedule)
    }

    override fun getDescription(): String {
        return name
    }

    override fun isDisabled(): Boolean {
        return false
    }

}
