package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.queue.source.createQueueSource
import net.nemerosa.ontrack.extension.scm.SCMJobs
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogEnabled
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class ScmSearchIndexJobs(
    private val securityService: SecurityService,
    private val structureService: StructureService,
    private val scmDetector: SCMDetector,
    private val queueDispatcher: QueueDispatcher,
    private val scmSearchIndexQueueProcessor: ScmSearchIndexQueueProcessor,
    private val scmSearchIndexQueueSourceExtension: ScmSearchIndexQueueSourceExtension,
) : JobOrchestratorSupplier {

    override val jobRegistrations: Collection<JobRegistration>
        get() = securityService.asAdmin {
            structureService.projectList.mapNotNull {
                createProjectScmSearchIndexJobRegistration(it)
            }
        }

    private fun createProjectScmSearchIndexJobRegistration(project: Project): JobRegistration? {
        val scm = scmDetector.getSCM(project) ?: return null
        if (scm !is SCMChangeLogEnabled) return null

        val indexationInterval = scm.indexationInterval
        if (indexationInterval <= 0) return null

        return JobRegistration(
            job = createProjectScmSearchIndexJob(project),
            schedule = Schedule.everyMinutes(indexationInterval.toLong()),
        )
    }

    private fun createProjectScmSearchIndexJob(
        project: Project,
    ) = object : Job {

        override fun getKey(): JobKey = SCMJobs.category
            .getType("indexation").withName("SCM Indexation")
            .getKey(project.name)

        override fun getDescription(): String = "SCM indexation for ${project.name}"

        override fun isDisabled(): Boolean = project.isDisabled

        override fun getTask() = JobRun {
            queueDispatcher.dispatch(
                queueProcessor = scmSearchIndexQueueProcessor,
                payload = ScmSearchIndexQueueItem(project.name),
                source = scmSearchIndexQueueSourceExtension.createQueueSource(
                    data = ScmSearchIndexQueueSourceData(project.name)
                )
            )
        }
    }

}