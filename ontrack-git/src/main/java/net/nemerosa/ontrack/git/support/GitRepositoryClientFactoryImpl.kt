package net.nemerosa.ontrack.git.support;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.git.GitRepositoryClientFactory;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryDirException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class GitRepositoryClientFactoryImpl implements GitRepositoryClientFactory {

    private final File root;

    private final Cache<String, GitRepositoryClient> repositoryClientCache =
            CacheBuilder.newBuilder()
                    .maximumSize(10)
                    .build();

    private final ReentrantLock lock = new ReentrantLock();

    public GitRepositoryClientFactoryImpl(File root) {
        this.root = root;
    }

    @Override
    public GitRepositoryClient getClient(GitRepository repository) {
        String remote = repository.getRemote();
        lock.lock();
        try {
            // Gets any existing repository in the cache
            GitRepositoryClient repositoryClient = repositoryClientCache.getIfPresent(remote);
            if (repositoryClient != null && repositoryClient.isCompatible(repository)) {
                return repositoryClient;
            }
            // Repository to be created
            else {
                return createAndRegisterRepositoryClient(repository);
            }
        } finally {
            lock.unlock();
        }
    }

    protected GitRepositoryClient createAndRegisterRepositoryClient(GitRepository repository) {
        GitRepositoryClient client = createRepositoryClient(repository);
        repositoryClientCache.put(repository.getRemote(), client);
        return client;
    }

    protected GitRepositoryClient createRepositoryClient(GitRepository repository) {
        // ID for this repository
        String repositoryId = repository.getId();
        // Directory for this repository
        File repositoryDir = new File(root, repositoryId);
        // Makes sure the directory is ready
        try {
            FileUtils.forceMkdir(repositoryDir);
        } catch (IOException ex) {
            throw new GitRepositoryDirException(repositoryDir, ex);
        }
        // Creates the client
        return new GitRepositoryClientImpl(repositoryDir, repository);
    }

}
