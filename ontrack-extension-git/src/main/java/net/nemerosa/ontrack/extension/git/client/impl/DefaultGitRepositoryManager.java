package net.nemerosa.ontrack.extension.git.client.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.nemerosa.ontrack.model.support.EnvService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ExecutionException;

@Component
public class DefaultGitRepositoryManager implements GitRepositoryManager {

    private final Logger logger = LoggerFactory.getLogger(GitRepositoryManager.class);
    private final EnvService envService;
    private final LoadingCache<GitRepositoryKey, GitRepository> repositoryCache =
            CacheBuilder.newBuilder()
                    .maximumSize(10)
                    .build(new CacheLoader<GitRepositoryKey, GitRepository>() {
                        @Override
                        public GitRepository load(@SuppressWarnings("NullableProblems") GitRepositoryKey key) throws Exception {
                            return createRepositoryManager(key);
                        }
                    });

    @Autowired
    public DefaultGitRepositoryManager(EnvService envService) {
        this.envService = envService;
    }

    private synchronized GitRepository createRepositoryManager(GitRepositoryKey key) {
        String remote = key.getRemote();
        String branch = key.getBranch();
        logger.info("[git-repository] Creating repository manager for {} and branch {}", remote, branch);
        // Gets the ID for this remote location
        String id = getRepositoryId(remote, branch);
        logger.info("[git-repository] Repository manager id for {} and branch {} is {}", remote, branch, id);
        // Gets the working directory for this ID
        File wd = envService.getWorkingDir("git", String.format("wd-%s", id));
        logger.debug("[git-repository] Repository manager working dir for {} is at {}", id, wd);
        // Creates the repository manager
        return new DefaultGitRepository(wd, remote, branch, id);
    }

    @Override
    public GitRepository getRepository(String remote, String branch) {
        // Gets the cached repository managed or creates it
        try {
            return repositoryCache.get(new GitRepositoryKey(remote, branch));
        } catch (ExecutionException e) {
            throw new GitRepositoryManagerException(remote, e);
        }
    }

    protected String getRepositoryId(String remote, String branch) {
        return (remote + "_" + branch).replaceAll("[:\\.\\\\/@]", "_");
    }

}
