package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.git.exceptions.GitRepositoryDirException
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.time.Duration
import java.util.concurrent.locks.ReentrantLock

class GitRepositoryClientFactoryImpl(
    private val root: File,
    private val timeout: Duration = Duration.ofSeconds(60),
    private val operationTimeout: Duration = Duration.ofMinutes(10),
    private val retries: UInt = 3u,
    private val interval: Duration = Duration.ofSeconds(30),
) : GitRepositoryClientFactory {

    private val logger: Logger = LoggerFactory.getLogger(GitRepositoryClientFactoryImpl::class.java)

    private val lock = ReentrantLock()

    override fun reset() {
        lock.lock()
        try {
            // Removing all directories
            root.listFiles()?.forEach { f ->
                if (f.isDirectory) {
                    val name = f.name
                    try {
                        FileUtils.deleteDirectory(f)
                        logger.debug("Deleted $name")
                    } catch (any: Exception) {
                        logger.error("Cannot delete $name", any)
                    }
                }
            }
        } finally {
            lock.unlock()
        }
    }

    override fun getClient(repository: GitRepository): GitRepositoryClient {
        lock.lock()
        try {
            // Gets any existing repository in the cache
            return createRepositoryClient(repository)
        } finally {
            lock.unlock()
        }
    }

    private fun createRepositoryClient(repository: GitRepository): GitRepositoryClient {
        // ID for this repository
        val repositoryId = repository.id
        // Directory for this repository
        val repositoryDir = File(root, repositoryId)
        // Makes sure the directory is ready
        try {
            FileUtils.forceMkdir(repositoryDir)
        } catch (ex: IOException) {
            throw GitRepositoryDirException(repositoryDir, ex)
        }

        // Creates the client
        return GitRepositoryClientImpl(
            repositoryDir = repositoryDir,
            repository = repository,
            timeout = timeout,
            operationTimeout = operationTimeout,
            retries = retries,
            interval = interval
        )
    }

}
