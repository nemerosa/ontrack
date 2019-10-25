package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.git.exceptions.GitRepositoryDirException
import org.apache.commons.io.FileUtils
import org.springframework.cache.CacheManager
import java.io.File
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock

class GitRepositoryClientFactoryImpl(
        private val root: File,
        private val cacheManager: CacheManager
) : GitRepositoryClientFactory {

    companion object {
        const val CACHE_GIT_REPOSITORY_CLIENT = "gitRepositoryClient"
    }

    private val lock = ReentrantLock()

    override fun getClient(repository: GitRepository): GitRepositoryClient {
        val remote = repository.remote
        lock.lock()
        try {
            // Gets any existing repository in the cache
            val repositoryClient = cacheManager.getCache(CACHE_GIT_REPOSITORY_CLIENT)?.get(remote)?.get() as? GitRepositoryClient?
            return if (repositoryClient != null && repositoryClient.isCompatible(repository)) {
                repositoryClient
            } else {
                createAndRegisterRepositoryClient(repository)
            }// Repository to be created
        } finally {
            lock.unlock()
        }
    }

    private fun createAndRegisterRepositoryClient(repository: GitRepository): GitRepositoryClient {
        val client = createRepositoryClient(repository)
        cacheManager.getCache(CACHE_GIT_REPOSITORY_CLIENT)?.put(repository.remote, client)
        return client
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
        return GitRepositoryClientImpl(repositoryDir, repository)
    }

}
