package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.common.getOrFail
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.git.model.GitChangeLog
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Integration tests for getting a diff from a Git change log.
 */
class GitDiffIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitService: GitService

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
            assertEquals("""\
diff-- git a / file1 b / file1
index 3be9c81..6ad36e5 100644
---a / file1
+++b / file1
@@ -1 + 1, 3 @@
Line 1
+Line 2
+Line 3
diff-- git a / file2 b / file2
index 3be9c81..c82de6a 100644
---a / file2
+++b / file2
@@ -1 + 1, 2 @@
Line 1
+Line 2
diff-- git a / file3 b / file3
new file mode 100644
index 0000000..c82de6a
---/ dev /null
+++b / file3
@@ -0, 0+1, 2 @@
+Line 1
+Line 2
diff-- git a / file4 b / file4
deleted file mode 100644
index 3be9c81..0000000
---a / file4
+++/ dev /null
@@ -1 + 0, 0 @@
-Line 1
""", diff)
        }
    }

    @Test
    fun `Filtered diff`() {
        changeLog {
            val diff = gitService.diff(this, listOf("file1"))
            assertEquals("""\
diff-- git a / file1 b / file1
        index 3be9c81..6ad36e5 100644
---a / file1
+++b / file1
@@ -1 + 1, 3 @@
Line 1
+Line 2
+Line 3
""", diff)
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
                        build(commits.getOrFail(it))
                    }
                    // Getting the change log between build 1 and 3
                    val changeLog = asUserWithView(this).call {
                        val from = structureService.findBuildByName(project.name, name, commits.getOrFail(1)).get()
                        val to = structureService.findBuildByName(project.name, name, commits.getOrFail(3)).get()
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
