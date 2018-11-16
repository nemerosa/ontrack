package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.extension.git.support.TagBuildNameGitCommitLink
import net.nemerosa.ontrack.git.support.GitRepo
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Integration tests for Git support.
 */
class GitChangeLogIT extends AbstractServiceTestSupport {

    @Autowired
    private GitConfigurationService gitConfigurationService

    @Autowired
    private StructureService structureService

    @Autowired
    private PropertyService propertyService

    @Autowired
    private GitService gitService

    @Autowired
    private JobOrchestrator jobOrchestrator

    @Test
    void 'Change log based on commits'() {

        def repo = new GitRepo()
        try {

            // Creates a Git repository with 10 commits
            repo.with {
                git 'init'
                (1..10).each { commit it }
                git 'log', '--oneline'
            }

            // Identifies the commits
            def commits = [:]
            (1..10).each {
                commits[it as String] = repo.commitLookup("Commit $it")
            }

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

            // Configures the project
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
                                        new CommitBuildNameGitCommitLink(),
                                        new CommitLinkConfig(true)
                                ).toServiceConfiguration(),
                                false, 0
                        )
                )
            }

            // Job registration
            asAdmin().execute {
                jobOrchestrator.orchestrate(JobRunListener.out())
            }

            // Creates builds for some commits
            asUser().with(project, ProjectEdit).call {
                [2, 5, 7, 8].each {
                    sleep 100 // Some delay to get correct timestamps in builds
                    def buildName = commits[it as String] as String
                    println "Creating build $buildName"
                    structureService.newBuild(
                            Build.of(
                                    branch,
                                    NameDescription.nd(buildName, "Build $it"),
                                    Signature.of('test')
                            )
                    )
                }
            }

            // Getting the change log between build 5 and 7
            asUser().with(project, ProjectView).call {

                BuildDiffRequest buildDiffRequest = new BuildDiffRequest()
                buildDiffRequest.from = structureService.findBuildByName(project.name, branch.name, commits['5'] as String).get().id
                buildDiffRequest.to = structureService.findBuildByName(project.name, branch.name, commits['7'] as String).get().id
                def changeLog = gitService.changeLog(buildDiffRequest)

                // Getting the commits
                def changeLogCommits = gitService.getChangeLogCommits(changeLog)

                // Checks the commits
                def messages = changeLogCommits.log.commits.collect { it.commit.shortMessage }
                assert messages == ['Commit 7', 'Commit 6']

            }

        } finally {
            repo.close()
        }

    }

    @Test
    void 'Change log based on tags'() {

        def repo = new GitRepo()
        try {

            def tags = [2, 5, 7, 8]

            // Creates a Git repository with 10 commits
            repo.with {
                git 'init'
                (1..10).each {
                    commit it
                    if (it in tags) {
                        git 'tag', "v$it"
                    }
                }
                git 'log', '--oneline', '--decorate'
            }

            // Identifies the commits
            def commits = [:]
            (1..10).each {
                commits[it as String] = repo.commitLookup("Commit $it")
            }

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

            // Configures the project
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
                                TagBuildNameGitCommitLink.DEFAULT.toServiceConfiguration(),
                                false, 0
                        )
                )
            }

            // Job registration
            asAdmin().execute {
                jobOrchestrator.orchestrate(JobRunListener.out())
            }

            // Creates builds for some tags
            asUser().with(project, ProjectEdit).call {
                tags.each {
                    sleep 100 // Some delay to get correct timestamps in builds
                    def buildName = "v$it"
                    println "Creating build $buildName"
                    structureService.newBuild(
                            Build.of(
                                    branch,
                                    NameDescription.nd(buildName, "Build $it"),
                                    Signature.of('test')
                            )
                    )
                }
            }

            // Getting the change log between build 5 and 7
            asUser().with(project, ProjectView).call {

                BuildDiffRequest buildDiffRequest = new BuildDiffRequest()
                buildDiffRequest.from = structureService.findBuildByName(project.name, branch.name, 'v5').get().id
                buildDiffRequest.to = structureService.findBuildByName(project.name, branch.name, 'v7').get().id
                def changeLog = gitService.changeLog(buildDiffRequest)

                // Getting the commits
                def changeLogCommits = gitService.getChangeLogCommits(changeLog)

                // Checks the commits
                def messages = changeLogCommits.log.commits.collect { it.commit.shortMessage }
                assert messages == ['Commit 7', 'Commit 6']

            }

        } finally {
            repo.close()
        }

    }

}
