package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.PropertyService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid

class GitIndexationJobIT extends AbstractServiceTestSupport {

    @Autowired
    private GitConfigurationService gitConfigurationService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private JobScheduler jobScheduler

    @Autowired
    private JobOrchestrator jobOrchestrator

    /**
     * Regression test for #434. Checks that changing a Git configuration does change the associated indexation job
     * for a project.
     */
    @Test
    void 'Git configuration change changes the indexation job'() {
        GitRepo.prepare { GitRepo repo ->

            // Some content
            git 'init'
            commit 1, '#1'
            commit 2, '#2'
            git 'log', '--oneline', '--decorate'

            // Create a Git configuration
            String gitConfigurationName = uid('C')
            BasicGitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
                gitConfigurationService.newConfiguration(
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                                .withRemote("file://${repo.dir.absolutePath}")
                )
            }

            // Creates a project
            def project = doCreateProject()

            // Configures the project
            asUser().with(project, ProjectEdit).call {
                propertyService.editProperty(
                        project,
                        GitProjectConfigurationPropertyType,
                        new GitProjectConfigurationProperty(gitConfiguration)
                )
            }

            // Runs the orchestration
            jobOrchestrator.orchestrate(JobRunListener.out());

            // Checks that the indexation job is registered
            def statuses = jobScheduler.getJobStatuses()
            def status = statuses.find {
                it.description == "file://${repo.dir.absolutePath} (${gitConfigurationName} @ basic)" as String
            }
            assert status != null: "The indexation job must be present"

            // Creates a new repository all together
            GitRepo newRepo = new GitRepo()
            newRepo.with {
                git 'init'
                commit 1, '#1'
                commit 2, '#2'
                git 'log', '--oneline', '--decorate'
            }

            // Updates the configuration
            asUser().with(GlobalSettings).call {
                gitConfigurationService.updateConfiguration(
                        gitConfigurationName,
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withIssueServiceConfigurationIdentifier(MockIssueServiceConfiguration.INSTANCE.toIdentifier().format())
                                .withRemote("file://${newRepo.dir.absolutePath}")
                )
            }

            // Runs the orchestration
            jobOrchestrator.orchestrate(JobRunListener.out());

            // Checks that the NEW indexation job is registered
            statuses = jobScheduler.getJobStatuses()
            status = statuses.find {
                it.description == "file://${newRepo.dir.absolutePath} (${gitConfigurationName} @ basic)" as String
            }
            assert status != null: "The new indexation job must be present"
            // Checks that the OLD indexation job is gone
            status = statuses.find {
                it.description == "file://${repo.dir.absolutePath} (${gitConfigurationName} @ basic)" as String
            }
            assert status == null: "The old indexation job must be done"
        }
    }

}
