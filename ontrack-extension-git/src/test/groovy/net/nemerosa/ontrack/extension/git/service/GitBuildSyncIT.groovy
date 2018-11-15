package net.nemerosa.ontrack.extension.git.service


import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.TagPatternBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.scm.support.TagPattern
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.git.support.GitRepo.prepare
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Testing the sync between builds and Git tags.
 */
class GitBuildSyncIT extends AbstractServiceTestSupport {

    @Autowired
    private GitService gitService
    @Autowired
    private StructureService structureService
    @Autowired
    private PropertyService propertyService
    @Autowired
    private GitRepositoryClientFactory gitClientFactory
    @Autowired
    private GitConfigurationService gitConfigurationService
    @Autowired
    private TagPatternBuildNameGitCommitLink tagPatternBuildNameGitCommitLink
    @Autowired
    private JobOrchestrator jobOrchestrator

    @Test
    void 'Master sync'() {
        // Git repo
        prepare {

            int no = 0
            gitInit()

            commit no++
            git 'tag', '1.1.6'
            commit no++
            git 'tag', '1.1.7'
            commit no++
            git 'tag', '1.2.0'
            commit no++
            git 'tag', '1.2.1'
            commit no
            git 'tag', '1.2.2'

            git 'log', '--oneline', '--graph', '--decorate', '--all'

        } and { client, repo ->

            // Create a Git configuration
            String gitConfigurationName = uid('C')
            BasicGitConfiguration gitConfiguration = asUser().with(GlobalSettings).call {
                gitConfigurationService.newConfiguration(
                        BasicGitConfiguration.empty()
                                .withName(gitConfigurationName)
                                .withRemote("file://${repo.dir.absolutePath}")
                )
            }

            // Creates a project and branch
            Branch branch = doCreateBranch()
            Project project = branch.project

            // Configures the project and the branch
            asUser().with(project, ProjectEdit).call {
                propertyService.editProperty(
                        project,
                        GitProjectConfigurationPropertyType,
                        new GitProjectConfigurationProperty(gitConfiguration)
                )
                // ...  & the branch with a link based on commits
                propertyService.editProperty(
                        branch,
                        GitBranchConfigurationPropertyType,
                        new GitBranchConfigurationProperty(
                                'master',
                                new ConfiguredBuildGitCommitLink<>(
                                        tagPatternBuildNameGitCommitLink,
                                        new TagPattern('1.2.*')
                                ).toServiceConfiguration(),
                                false, 1
                        )
                )

                // Job registration
                jobOrchestrator.orchestrate(JobRunListener.out());

                // Build synchronisation
                gitService.launchBuildSync(branch.id, true)

                // Checks the builds have been created
                assert !structureService.findBuildByName(project.name, branch.name, '1.1.6').present
                assert !structureService.findBuildByName(project.name, branch.name, '1.1.7').present
                assert structureService.findBuildByName(project.name, branch.name, '1.2.0').present
                assert structureService.findBuildByName(project.name, branch.name, '1.2.1').present
                assert structureService.findBuildByName(project.name, branch.name, '1.2.2').present
            }
        }
    }

}
