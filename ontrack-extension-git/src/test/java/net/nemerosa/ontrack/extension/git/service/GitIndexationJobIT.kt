package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration
import net.nemerosa.ontrack.git.support.GitRepo
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GitIndexationJobIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var gitConfigurationService: GitConfigurationService

    @Autowired
    private lateinit var jobScheduler: JobScheduler

    @Autowired
    private lateinit var jobOrchestrator: JobOrchestrator

    /**
     * Regression test for #434. Checks that changing a Git configuration does change the associated indexation job
     * for a project.
     */
    @Test
    fun `Git configuration change changes the indexation job`() {
        GitRepo.prepare {

            // Some content
            gitInit()
            commit(1, "#1")
            commit(2, "#2")
            log()

            // Create a Git configuration
            val gitConfigurationName = uid("C")
            val gitConfiguration = asUser().with(GlobalSettings::class.java).call {
                gitConfigurationService.newConfiguration(
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                                .withRemote("file://${dir.absolutePath}")
                )
            }

            // Creates a project
            val project = doCreateProject()

            // Configures the project
            asUser().with(project, ProjectEdit::class.java).call {
                propertyService.editProperty(
                        project,
                        GitProjectConfigurationPropertyType::class.java,
                        GitProjectConfigurationProperty(gitConfiguration)
                )
            }

            // Runs the orchestration
            asAdmin().execute {
                jobOrchestrator.orchestrate(JobRunListener.out())
            }

            // Checks that the indexation job is registered
            var statuses = jobScheduler.jobStatuses
            var status = statuses.find {
                it.description == "file://${dir.absolutePath} ($gitConfigurationName @ basic)" as String
            }
            assertNotNull(status, "The indexation job must be present")

            // Creates a new repository all together
            val newRepo = GitRepo()
            newRepo.apply {
                gitInit()
                commit(1, "#1")
                commit(2, "#2")
                log()
            }

            // Updates the configuration
            asUser().with(GlobalSettings::class.java).call {
                gitConfigurationService.updateConfiguration(
                        gitConfigurationName,
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                                .withRemote("file://${newRepo.dir.absolutePath}")
                )
            }

            // Runs the orchestration
            asAdmin().execute {
                jobOrchestrator.orchestrate(JobRunListener.out())
            }

            // Checks that the NEW indexation job is registered
            statuses = jobScheduler.jobStatuses
            status = statuses.find {
                it.description == "file://${newRepo.dir.absolutePath} (${gitConfigurationName} @ basic)" as String
            }
            assertNotNull(status, "The new indexation job must be present")
            // Checks that the OLD indexation job is gone
            status = statuses.find {
                it.description == "file://${dir.absolutePath} ($gitConfigurationName @ basic)" as String
            }
            assertNull(status, "The old indexation job must be done")
        }
    }

}
