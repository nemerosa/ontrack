package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.git.model.GitChangeLog
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Integration tests for getting a diff from a Git change log.
 */
class GitDiffIT : AbstractGitTestSupport() {

    private lateinit var oldPrefix: String
    private lateinit var newPrefix: String

    @Before
    fun `Load user git diff config`() {
        val userGitDiffFormat = DiffFormatter(ByteArrayOutputStream())
        userGitDiffFormat.setRepository(FileRepository(""))
        oldPrefix = userGitDiffFormat.oldPrefix
        newPrefix = userGitDiffFormat.newPrefix
    }

    @Test
    fun `File change indicator`() {
        changeLog {
            val changeLogFiles = gitService.getChangeLogFiles(this)

            // Updated file
            var changeLogFile = changeLogFiles.list.find { it.path == "file1" }
            assertNotNull(changeLogFile) {
                assertEquals(
                        SCMChangeLogFileChangeType.MODIFIED,
                        it.changeType
                )
            }
            // Added file
            changeLogFile = changeLogFiles.list.find { it.path == "file3" }
            assertNotNull(changeLogFile) {
                assertEquals(
                        SCMChangeLogFileChangeType.ADDED,
                        it.changeType
                )
            }
            // Deleted file
            changeLogFile = changeLogFiles.list.find { it.path == "file4" }
            assertNotNull(changeLogFile) {
                assertEquals(
                        SCMChangeLogFileChangeType.DELETED,
                        it.changeType
                )
            }
        }
    }

    @Test
    fun `Full diff`() {
        changeLog {
            val diff = gitService.diff(this, emptyList())
            assertEquals("""
diff --git ${oldPrefix}file1 ${newPrefix}file1
index 3be9c81..6ad36e5 100644
--- ${oldPrefix}file1
+++ ${newPrefix}file1
@@ -1 +1,3 @@
 Line 1
+Line 2
+Line 3
diff --git ${oldPrefix}file2 ${newPrefix}file2
index 3be9c81..c82de6a 100644
--- ${oldPrefix}file2
+++ ${newPrefix}file2
@@ -1 +1,2 @@
 Line 1
+Line 2
diff --git ${oldPrefix}file3 ${newPrefix}file3
new file mode 100644
index 0000000..c82de6a
--- /dev/null
+++ ${newPrefix}file3
@@ -0,0 +1,2 @@
+Line 1
+Line 2
diff --git ${oldPrefix}file4 ${newPrefix}file4
deleted file mode 100644
index 3be9c81..0000000
--- ${oldPrefix}file4
+++ /dev/null
@@ -1 +0,0 @@
-Line 1
""".trim(), diff.trim())
        }
    }

    @Test
    fun `Filtered diff`() {
        changeLog {
            val diff = gitService.diff(this, listOf("file1"))
            assertEquals("""
diff --git ${oldPrefix}file1 ${newPrefix}file1
index 3be9c81..6ad36e5 100644
--- ${oldPrefix}file1
+++ ${newPrefix}file1
@@ -1 +1,3 @@
 Line 1
+Line 2
+Line 3
""".trim(), diff.trim())
        }
    }

    private fun changeLog(code: GitChangeLog.() -> Unit) {
        createRepo {
            file("file1", "Line 1\n")
            file("file2", "Line 1\n")
            file("file4", "Line 1\n")
            git("commit", "-m", "Commit 1")
            file("file1", "Line 1\nLine 2\n")
            file("file2", "Line 1\n")
            git("commit", "-m", "Commit 2")
            file("file1", "Line 1\nLine 2\nLine 3\n")
            file("file2", "Line 1\nLine 2\n")
            file("file3", "Line 1\nLine 2\n")
            delete("file4")
            git("commit", "-m", "Commit 3")
            (1..3).associate { it to commitLookup("Commit $it") }
        } and { repo, commits ->
            project {
                gitProject(repo)
                branch {
                    gitBranch {
                        buildNameAsCommit(abbreviated = true)
                    }
                    // Creates the builds for the commits
                    (1..3).forEach {
                        build(commits.getValue(it))
                    }
                    // Getting the change log between build 1 and 3
                    val changeLog = asUserWithView(this).call {
                        val from = structureService.findBuildByName(project.name, name, commits.getValue(1)).get()
                        val to = structureService.findBuildByName(project.name, name, commits.getValue(3)).get()
                        val buildDiffRequest = BuildDiffRequest(from.id, to.id)
                        gitService.changeLog(buildDiffRequest)
                    }
                    // Running the test
                    changeLog.code()
                }
            }
        }
    }
}
