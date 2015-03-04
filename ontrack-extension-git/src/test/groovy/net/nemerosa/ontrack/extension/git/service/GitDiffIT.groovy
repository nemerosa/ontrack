package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.ConfiguredBuildGitCommitLink
import net.nemerosa.ontrack.extension.git.model.GitChangeLog
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationProperty
import net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.git.support.CommitBuildNameGitCommitLink
import net.nemerosa.ontrack.extension.git.support.CommitLinkConfig
import net.nemerosa.ontrack.git.support.GitRepo
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.test.TestUtils.uid
import static org.junit.Assert.assertEquals

/**
 * Integration tests for getting a diff from a Git change log.
 */
class GitDiffIT extends AbstractServiceTestSupport {

    @Autowired
    private GitService gitService

    @Autowired
    private GitConfigurationService gitConfigurationService

    @Autowired
    private StructureService structureService

    @Autowired
    private PropertyService propertyService

    private GitRepo repo
    private GitChangeLog changeLog

    @Before
    void before() {
        repo = new GitRepo()

        // Creates a Git repository with 10 commits
        repo.with {
            git 'init'
            file 'file1', 'Line 1\n'
            file 'file2', 'Line 1\n'
            git 'commit', '-m', 'Commit 1'
            file 'file1', 'Line 1\nLine 2\n'
            file 'file2', 'Line 1\n'
            git 'commit', '-m', 'Commit 2'
            file 'file1', 'Line 1\nLine 2\nLine 3\n'
            file 'file2', 'Line 1\nLine 2\n'
            git 'commit', '-m', 'Commit 3'
            git 'log', '--oneline', '--decorate'
        }

        // Identifies the commits
        def commits = [:]
        (1..3).each {
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

        // Creates builds for the commits
        asUser().with(project, ProjectEdit).call {
            (1..3).each {
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

        // Getting the change log between build 1 and 3
        changeLog = asUser().with(project, ProjectView).call {
            BuildDiffRequest buildDiffRequest = new BuildDiffRequest()
            buildDiffRequest.from = structureService.findBuildByName(project.name, branch.name, commits['1'] as String).get().id
            buildDiffRequest.to = structureService.findBuildByName(project.name, branch.name, commits['3'] as String).get().id
            return gitService.changeLog(buildDiffRequest)
        }
    }

    @After
    void after() {
        repo.close()
    }

    @Test
    void 'Full diff'() {
        String diff = gitService.diff(changeLog, [])
        assertEquals '''\
diff --git a/file1 b/file1
index 3be9c81..6ad36e5 100644
--- a/file1
+++ b/file1
@@ -1 +1,3 @@
 Line 1
+Line 2
+Line 3
diff --git a/file2 b/file2
index 3be9c81..c82de6a 100644
--- a/file2
+++ b/file2
@@ -1 +1,2 @@
 Line 1
+Line 2
''', diff
    }

    @Test
    void 'Filtered diff'() {
        String diff = gitService.diff(changeLog, ['file1'])
        assertEquals '''\
diff --git a/file1 b/file1
index 3be9c81..6ad36e5 100644
--- a/file1
+++ b/file1
@@ -1 +1,3 @@
 Line 1
+Line 2
+Line 3
''', diff
    }
}
