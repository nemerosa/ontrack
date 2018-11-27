package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.common.Utils
import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.git.GitRepositoryClient
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * Utility class to deal with a Git repository.
 */
class GitRepo(val dir: File): AutoCloseable {
    constructor() : this(createTempDir("ontrack-git"))

    companion object {

        /**
         * Preparing a repository
         */
        @JvmStatic
        fun prepare(preparation: GitRepo.() -> Unit): GitRepoOperations {
            // Creates the repository
            val origin = GitRepo()
            // Prepares the repository
            origin.preparation()
            // Returns operations on this repository
            return GitRepoOperations(origin)
        }

        /**
         * Cloning a local test repository
         */
        @JvmStatic
        fun cloneRepo(wd: File, origin: GitRepo): GitRepositoryClient {
            // Repository definition for the `origin` repository
            val originRepository = GitRepository(
                    "file",
                    "test",
                    origin.dir.absolutePath,
                    "", ""
            )
            // Creates the client
            return GitRepositoryClientImpl(
                    wd,
                    originRepository
            )
        }

    }

    /**
     * Gets a client to drive this repository
     */
    val client: GitRepositoryClient
        get() = GitRepositoryClientImpl(
                dir,
                GitRepository("test", "test", "", "", "")
        )

    /**
     * Closes this repository
     */
    override fun close() {
        FileUtils.deleteDirectory(dir)
    }

    override fun toString(): String {
        return dir.toString()
    }

    /**
     * Runs a Git command and returns its output
     */
    fun git(vararg args: String): String =
            cmd("git", *args)

    /**
     * Runs an arbitrary command in the working directory
     */
    fun cmd(executable: String, vararg args: String): String {
        val output = Utils.run(dir, executable, *args)
        println(output)
        return output
    }

    /**
     * Creates or updates a file with some content, and optionally adds it to the index
     */
    @JvmOverloads
    fun file(path: String, content: String, add: Boolean = true) {
        val file = File(dir, path)
        file.parentFile.mkdirs()
        file.writeText(content)
        if (add) {
            git("add", path)
        }
    }

    /**
     * Deletes a file
     */
    fun delete(path: String) {
        val file = File(dir, path)
        if (file.exists()) {
            git("rm", path)
        }
    }

    @JvmOverloads
    fun commit(no: Any, message: String? = null): String {
        val fileName = "file$no"
        cmd("touch", fileName)
        git("add", fileName)
        val commitMessage = message ?: "Commit $no"
        return git("commit", "-m", commitMessage)
    }

    @JvmOverloads
    fun commitLookup(message: String, shortId: Boolean = true): String {
        val commitFormat = if (shortId) "%h" else "%H"
        val info = git("log", "--all", "--grep", message, "--pretty=format:$commitFormat")
        if (info.isNotBlank()) {
            return info.trim()
        } else {
            throw RuntimeException("Cannot find commit for message $message")
        }
    }

    fun log() {
        git("log", "--oneline", "--graph", "--decorate", "--all")
    }

    fun gitInit() {
        git("init")
    }

    fun tag(name: String) {
        git("tag", name)
    }

    class GitRepoOperations(private val repo: GitRepo) {
        /**
         * Chaining of operations
         */
        infix fun and(clientAction: (GitRepositoryClient, GitRepo) -> Unit): GitRepoOperations {
            clientAction(repo.client, repo)
            return this
        }

        /**
         * Clones this repository and performs some operation on it
         */
        infix fun withClone(clientAction: (GitRepositoryClient, GitRepo, GitRepo) -> Unit) {
            try {
                val wd = createTempDir("ontrack-git", "")
                try {
                    // Client
                    val client = cloneRepo(wd, repo)
                    // Utility test access
                    val clientRepo = GitRepo(wd)
                    // Runs the action
                    clientAction(client, clientRepo, repo)
                } finally {
                    FileUtils.deleteDirectory(wd)
                }
            } finally {
                repo.close()
            }
        }
    }

}