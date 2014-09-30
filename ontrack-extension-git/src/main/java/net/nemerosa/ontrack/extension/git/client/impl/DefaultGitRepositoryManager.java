package net.nemerosa.ontrack.extension.git.client.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.nemerosa.ontrack.model.support.EnvService;
import net.nemerosa.ontrack.model.support.UserPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Component
public class DefaultGitRepositoryManager implements GitRepositoryManager {

    private final Logger logger = LoggerFactory.getLogger(GitRepositoryManager.class);
    private final EnvService envService;
    private final Cache<GitRepositoryKey, GitRepository> repositoryCache =
            CacheBuilder.newBuilder()
                    .maximumSize(10)
                    .build();

    @Autowired
    public DefaultGitRepositoryManager(EnvService envService) {
        this.envService = envService;
    }

    private synchronized GitRepository createRepositoryManager(String remote, String branch, Supplier<Optional<UserPassword>> userPasswordSupplier) {
        logger.info("[git-repository] Creating repository manager for {} and branch {}", remote, branch);
        // Gets the ID for this remote location
        String id = getRepositoryId(remote, branch);
        logger.info("[git-repository] Repository manager id for {} and branch {} is {}", remote, branch, id);
        // Gets the working directory for this ID
        File wd = envService.getWorkingDir("git", String.format("wd-%s", id));
        logger.debug("[git-repository] Repository manager working dir for {} is at {}", id, wd);
        // Creates the repository manager
        return new DefaultGitRepository(wd, remote, branch, id, userPasswordSupplier);
    }

    @Override
    public GitRepository getRepository(String remote, String branch, Supplier<Optional<UserPassword>> userPasswordSupplier) {
        // Gets the cached repository managed or creates it
        try {
            return repositoryCache.get(new GitRepositoryKey(remote, branch), () -> createRepositoryManager(remote, branch, userPasswordSupplier));
        } catch (ExecutionException e) {
            throw new GitRepositoryManagerException(remote, e);
        }
    }

    protected String getRepositoryId(String remote, String branch) {
        return (remote + "_" + branch).replaceAll("[:\\.\\\\/@]", "_");
    }

}
