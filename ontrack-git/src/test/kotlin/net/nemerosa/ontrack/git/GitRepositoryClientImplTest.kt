package net.nemerosa.ontrack.git

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.git.support.GitRepo
import org.junit.Test
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitRepositoryClientImplTest {

    /**
     * <pre>
     *     *   C4 (master)
     *     | * C3 (2.1)
     *     |/
     *     * C2
     *     * C1
     * </pre>
     */
    @Test
    fun `List of local branches with their commits`() {
        GitRepo.prepare {
            gitInit()
            commit(1)
            commit(2)
            git("checkout", "-b", "2.1")
            commit(3)
            git("checkout", "master")
            commit(4)

            log()
        } and { _, repo ->
            GitRepo.prepare {
                git("clone", repo.dir.absolutePath, ".")
            } and { cloneClient, _ ->
                val branches = cloneClient.branches
                assertEquals(2, branches.branches.size)

                assertEquals("2.1", branches.branches[0].name)
                assertEquals("Commit 3", branches.branches[0].commit.shortMessage)

                assertEquals("master", branches.branches[1].name)
                assertEquals("Commit 4", branches.branches[1].commit.shortMessage)
            }
        }
    }

    @Test
    fun `Log between HEAD and a commit ~ 1`() {
        GitRepo.prepare {
            gitInit()
            (1..6).forEach {
                commit(it)
            }

            log()
        } and { repoClient, repo ->
            val commit4 = repo.commitLookup("Commit 4")
            val log = repoClient.log("$commit4~1", "HEAD").collect(Collectors.toList())
            assertEquals(
                    listOf("Commit 6", "Commit 5", "Commit 4"),
                    log.map { it.shortMessage }
            )
        }
    }

    @Test
    fun `Graph between commits`() {
        GitRepo.prepare {
            gitInit()
            commit(1)
            commit(2)
            commit(3)
            tag("v3")
            commit(4)
            tag("v4")
            commit(5)
            commit(6)
            commit(7)
            tag("v7")
            commit(8)

            log()
        } and { repoClient, repo ->
            val commit4 = repo.commitLookup("Commit 4")
            val commit7 = repo.commitLookup("Commit 7")
            val log = repoClient.graph(commit7, commit4)
            assertEquals(
                    listOf("Commit 7", "Commit 6", "Commit 5"),
                    log.commits.map { it.shortMessage }
            )
        }
    }

    /**
     * What is the change log for 2.2 since 2.1?
     * <pre>
     *     | * C11 (v2.2, 2.2)
     *     * | C10
     *     | * C9
     *     | * C8
     *     |/
     *     * C7
     *     * M6
     *     |\
     *     | * C5 (v2.1, 2.1)
     *     * | C4
     *     | * C3
     *     |/
     *     * C2
     *     * C1
     * </pre>
     *
     * We expect C4, M6, C7, C8, C10, C11
     */
    @Test
    fun `Log between tags on different branches`() {
        GitRepo.prepare {
            gitInit()
            commit(1)
            commit(2)
            git("checkout", "-b", "2.1")
            commit(3)
            git("checkout", "master")
            commit(4)
            git("checkout", "2.1")
            commit(5)
            tag("v2.1")
            git("checkout", "master")
            git("merge", "--no-ff", "2.1", "--message", "Merge 2.1") // M6
            commit(7)
            git("checkout", "-b", "2.2")
            commit(8)
            commit(9)
            git("checkout", "master")
            commit(10)
            git("checkout", "2.2")
            commit(11)
            tag("v2.2")

            log()
        } and { repoClient, _ ->
            val log = repoClient.graph("v2.2", "v2.1")
            assertEquals(
                    listOf("Commit 11", "Commit 9", "Commit 8", "Commit 7", "Merge 2.1", "Commit 4"),
                    log.commits.map {
                        it.shortMessage
                    }
            )
        }
    }

    /**
     * What is the change log for 2.2 since 2.1?
     *
     * We expect C4, C7, C8, C10, C11, Merge 2.1->2.2, C14
     *
     * @see #prepareBranches(GitRepo)
     */
    @Test
    fun `Log between tags on different hierarchical branches`() {
        GitRepo.prepare {
            prepareBranches()
        } and { repoClient, _ ->
            val log = repoClient.graph("v2.2", "v2.1")
            assertEquals(
                    listOf(
                            "Commit 14",
                            "Merge 2.1->2.2",
                            "Commit 11",
                            "Commit 10",
                            "Commit 8",
                            "Commit 7",
                            "Commit 4"),
                    log.commits.map { it.shortMessage }
            )
        }
    }

    /**
     * Getting the tag for a commit
     */
    @Test
    fun `Tag containing a commit`() {
        GitRepo.prepare {
            prepareBranches()
        } withClone { client, clientRepo, _ ->
            client.sync(Consumer { println(it) })
            // No further tag
            assertEquals(
                    emptyList(),
                    client.getTagsWhichContainCommit(clientRepo.commitLookup("Commit 13"))
            )
            // Exact tag
            assertEquals(
                    listOf("v2.2"),
                    client.getTagsWhichContainCommit(clientRepo.commitLookup("Commit 14"))
            )
            // Several tags, including current commit
            assertEquals(
                    listOf("v2.1", "v2.2"),
                    client.getTagsWhichContainCommit(clientRepo.commitLookup("Commit 12"))
            )
            // Several tags
            assertEquals(
                    listOf("v2.1", "v2.2"),
                    client.getTagsWhichContainCommit(clientRepo.commitLookup("Commit 9"))
            )
        }
    }

    @Test
    fun `Log between tags`() {
        GitRepo.prepare {
            gitInit()
            commit(1)
            commit(2)
            commit(3)
            tag("v3")
            commit(4)
            tag("v4")
            commit(5)
            commit(6)
            commit(7)
            tag("v7")
            commit(8)
            log()
        } and { client, _ ->
            val log = client.graph("v7", "v4")
            assertEquals(
                    listOf("Commit 7", "Commit 6", "Commit 5"),
                    log.commits.map { it.shortMessage }
            )
        }
    }

    @Test
    fun `Collection of remote branches`() {
        GitRepo.prepare {
            // Initialises a Git repository
            gitInit()

            // Commits 1..4, each one a branch
            (1..4).forEach {
                commit(it)
                git("branch", "feature/$it", "HEAD")
            }

            // Log
            log()
        } and { _, repo ->
            GitRepo.prepare {
                git("clone", repo.dir.absolutePath, ".")
            } and { cloneClient, _ ->
                val branches = cloneClient.remoteBranches
                // Checks the list
                assertEquals(
                        (1..4).map { "feature/$it" } + listOf("master"),
                        branches.sorted()
                )
            }
        }
    }

    @Test
    fun `Get tags`() {
        GitRepo.prepare {
            prepareBranches()
        } withClone { client, _, _ ->
            client.sync(Consumer { println(it) })
            val expectedDate = Time.now().toLocalDate()
            assertEquals(
                    listOf("v2.1", "v2.2"),
                    client.tags.map { it.name }
            )
            assertEquals(
                    listOf(expectedDate, expectedDate),
                    client.tags.map { it.time.toLocalDate() }
            )
        }
    }

    @Test
    fun `Clone and fetch`() {
        GitRepo.prepare {
            // Initialises a Git repository
            gitInit()
            // Commits 1..4
            (1..4).forEach {
                commit(it)
            }
        } withClone { clone, cloneRepo, origin ->

            // First sync (clone)
            clone.sync(Consumer { println(it) })

            // Gets the commits
            (1..4).forEach {
                assertNotNull(cloneRepo.commitLookup("Commit $it"))
            }

            // Adds some commits on the origin repo
            origin.apply {
                (5..8).forEach {
                    commit(it)
                }
            }

            // Second sync (fetch)
            clone.sync(Consumer { println(it) })

            // Gets the commits
            (5..8).forEach {
                assertNotNull(cloneRepo.commitLookup("Commit $it"))
            }
        }
    }

    /**
     * Prepares some branches in a test repo.
     * <pre>
     *     | * C14 (v2.2, 2.2)
     *     * | C13
     *     | * Merge 2.1->2.2
     *     | |\_________________
     *     | |                  \
     *     | |                  * C12 (v2.1, 2.1)
     *     | * C11              |
     *     | * C10              |
     *     |/                   |
     *     |                    * C9
     *     * C8                 |
     *     * C7                 |
     *     |                    |
     *     |                    * C6
     *     |                    * C5
     *     *                    | C4
     *     |                    * C3
     *     |--------------------/
     *     * C2
     *     * C1
     * </pre>
     */
    private fun GitRepo.prepareBranches() {
        gitInit()
        commit(1)
        commit(2)
        git("checkout", "-b", "2.1")
        commit(3)
        git("checkout", "master")
        commit(4)
        git("checkout", "2.1")
        commit(5)
        commit(6)
        git("checkout", "master")
        commit(7)
        commit(8)
        git("checkout", "2.1")
        commit(9)
        git("checkout", "-b", "2.2", "master")
        commit(10)
        commit(11)
        git("checkout", "2.1")
        commit(12)
        tag("v2.1")
        git("checkout", "2.2")
        git("merge", "--no-ff", "2.1", "--message", "Merge 2.1->2.2")
        git("checkout", "master")
        commit(13)
        git("checkout", "2.2")
        commit(14)
        tag("v2.2")

        log()
    }

}