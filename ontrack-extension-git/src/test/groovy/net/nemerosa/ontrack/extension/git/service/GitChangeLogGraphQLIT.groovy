package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.property.*
import net.nemerosa.ontrack.extension.git.support.GitCommitPropertyCommitLink
import net.nemerosa.ontrack.git.support.GitRepo
import net.nemerosa.ontrack.graphql.AbstractQLITSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.NoConfig
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Integration tests for Git support.
 */
class GitChangeLogGraphQLIT extends AbstractQLITSupport {

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

    @Autowired
    private GitCommitPropertyCommitLink commitPropertyCommitLink

    void doTest(Closure testCode) {

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
                // ...  & the branch with a link based on commit property
                propertyService.editProperty(
                        branch,
                        GitBranchConfigurationPropertyType,
                        new GitBranchConfigurationProperty(
                                'master',
                                new ConfiguredBuildGitCommitLink<>(
                                        commitPropertyCommitLink,
                                        NoConfig.INSTANCE
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
                    def commit = commits[it as String] as String
                    def buildName = "${it}" as String
                    println "Creating build $buildName for commit $commit"
                    def build = doCreateBuild(
                            branch,
                            NameDescription.nd(buildName, "Build $it")
                    )
                    // Commit property
                    setProperty(
                            build,
                            GitCommitPropertyType.class,
                            new GitCommitProperty(commit)
                    )
                }
            }

            // Test
            testCode(branch)

        } finally {
            repo.close()
        }

    }

    @Test
    void 'Change log based on Git property'() {
        doTest { branch ->
            // Getting the change log between build 5 and 7
            def data = run("""{
                branches(id: ${branch.id}) {
                    gitChangeLog(from: "5", to: "7") {
                        commits {
                            commit {
                                shortMessage
                            }
                        }
                    }
                }
            }""")
            def messages = data.branches.first().gitChangeLog.commits*.commit.shortMessage
            assert messages == ['Commit 7', 'Commit 6']
        }
    }

    @Test
    void 'Change log based on Git property using root query'() {
        doTest { Branch branch ->
            // Gets the build ids
            int build5 = structureService.findBuildByName(
                    branch.project.name,
                    branch.name,
                    '5'
            ).orElseThrow({ new BuildNotFoundException(branch.project.name, branch.name, '5') }).id()
            int build7 = structureService.findBuildByName(
                    branch.project.name,
                    branch.name,
                    '7'
            ).orElseThrow({ new BuildNotFoundException(branch.project.name, branch.name, '5') }).id()
            // Getting the change log between build 5 and 7
            def data = run("""{
                gitChangeLog(from: ${build5}, to: ${build7}) {
                    commits {
                        commit {
                            shortMessage
                        }
                    }
                }
            }""")
            def messages = data.gitChangeLog.commits*.commit.shortMessage
            assert messages == ['Commit 7', 'Commit 6']
        }
    }

}
