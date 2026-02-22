package net.nemerosa.ontrack.extension.scm.search

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import net.nemerosa.ontrack.extension.scm.SCMExtensionConfigProperties
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogEnabled
import net.nemerosa.ontrack.extension.scm.changelog.SCMCommitFilter
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ScmSearchIndexServiceImpl(
    private val scmDetector: SCMDetector,
    private val scmExtensionConfigProperties: SCMExtensionConfigProperties,
    private val scmIndexIngestionRepository: ScmIndexIngestionRepository,
) : ScmSearchIndexService {

    // TODO Timeout
    // TODO Metrics

    override fun index(project: Project) {
        val scm = scmDetector.getSCM(project)
        if (scm != null && scm is SCMChangeLogEnabled) {
            runBlocking {
                val commitCount = withTimeoutOrNull(scmExtensionConfigProperties.search.database.timeout.toMillis()) {
                    indexInternal(
                        project = project,
                        scm = scm,
                    )
                }
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
        // TODO Launching the collection in a loop
        // TODO Saving the data in the database
        TODO()
    }

}