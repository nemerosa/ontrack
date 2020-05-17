package net.nemerosa.ontrack.job.orchestrator

import net.nemerosa.ontrack.job.Job
import net.nemerosa.ontrack.job.JobCategory.Companion.of
import net.nemerosa.ontrack.job.JobKey
import net.nemerosa.ontrack.job.JobRun
import net.nemerosa.ontrack.job.JobRunListener

/**
 * Job used for tests
 */
class TestJob(private val name: String) : Job {

    override fun getKey(): JobKey = getKey(name)

    override fun getTask(): JobRun = JobRun { runListener: JobRunListener -> runListener.message(name) }

    override fun getDescription(): String = name

    override fun isDisabled(): Boolean = false

    companion object {
        @JvmStatic
        fun getKey(name: String): JobKey = of("test").getType("orchestrator").getKey(name)
    }

}