package net.nemerosa.ontrack.extension.scm.search

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.scm.SCMExtensionConfigProperties
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogEnabled
import net.nemerosa.ontrack.extension.scm.changelog.SCMCommit
import net.nemerosa.ontrack.extension.scm.changelog.SCMCommitFilter
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.metrics.timeNotNull
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ScmSearchIndexServiceImpl(
    private val scmDetector: SCMDetector,
    private val scmExtensionConfigProperties: SCMExtensionConfigProperties,
    private val scmIndexIngestionRepository: ScmIndexIngestionRepository,
    private val scmIndexCommitRepository: ScmIndexCommitRepository,
    private val meterRegistry: MeterRegistry,
) : ScmSearchIndexService {

    override fun index(project: Project): Int {
        val scm = scmDetector.getSCM(project)
        return if (scm != null && scm is SCMChangeLogEnabled) {
            meterRegistry.timeNotNull(
                ScmSearchIndexMetrics.scmSearchIndexIndexationTime,
                ScmSearchIndexMetrics.TAG_PROJECT to project.name,
            ) {
                indexInternal(
                    project = project,
                    scm = scm,
                )
            }
        } else {
            throw ScmSearchIndexProjectSCMNotSupportedException(project.name)
        }
    }

    private fun indexInternal(project: Project, scm: SCMChangeLogEnabled): Int {
        // Gets the last ingestion data for this project
        val lastIngestion = scmIndexIngestionRepository.getLastIngestion(project.id())
        // Collection data for the SCM (since commit, since timestamp, count)
        val scmCommitFilter = SCMCommitFilter(
            sinceCommit = lastIngestion?.lastCommit,
            sinceCommitTimestamp = lastIngestion?.lastCommitTimestamp,
            count = scmExtensionConfigProperties.search.database.batchSize,
        )
        // Launching the collection in a loop
        var count = 0
        var lastScmCommit: SCMCommit? = null
        scm.forAllCommits(
            filter = scmCommitFilter,
        ) { scmCommit ->
            // Saving the data in the database
            scmIndexCommitRepository.save(
                project,
                ScmIndexCommit(
                    commitId = scmCommit.id,
                    commitShort = scmCommit.shortId,
                    commitTimestamp = scmCommit.timestamp,
                    message = scmCommit.message,
                )
            )
            // TODO Indexes the list of issues for this commit
//            if (issueConfig != null) {
//                val keys = issueConfig.extractIssueKeysFromMessage(commit.message)
//                projectIssueKeys.addAll(keys)
//            }
            lastScmCommit = scmCommit
            count++
        }
        // Last ingestion data
        scmIndexIngestionRepository.saveLastIngestion(
            projectId = project.id(),
            lastIngestion = lastIngestion,
            lastScmCommit = lastScmCommit,
        )
        // OK
        return count
    }

    override fun getCommits(
        project: Project,
        offset: Int,
        size: Int
    ): PaginatedList<ScmIndexCommit> =
        scmIndexCommitRepository.getCommits(project, offset, size)
}