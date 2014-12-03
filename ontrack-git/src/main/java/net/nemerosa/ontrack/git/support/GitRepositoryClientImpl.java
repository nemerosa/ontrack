package net.nemerosa.ontrack.git.support;

import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryAPIException;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryCannotCloneException;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryInitException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import static java.lang.String.format;

public class GitRepositoryClientImpl implements GitRepositoryClient {

    private final File repositoryDir;
    private final GitRepository repository;
    private final Git git;
    private final CredentialsProvider credentialsProvider;

    public GitRepositoryClientImpl(File repositoryDir, GitRepository repository) {
        this.repositoryDir = repositoryDir;
        this.repository = repository;
        // Gets the Git repository
        Repository gitRepository;
        try {
            gitRepository = new FileRepositoryBuilder()
                    .setWorkTree(repositoryDir)
                    .build();
        } catch (IOException e) {
            throw new GitRepositoryInitException(e);
        }
        // Gets the Git
        git = new Git(gitRepository);
        // Credentials
        if (StringUtils.isNotBlank(repository.getUser())) {
            credentialsProvider = new UsernamePasswordCredentialsProvider(repository.getUser(), repository.getPassword());
        } else {
            credentialsProvider = null;
        }
    }

    @Override
    public void sync(Consumer<String> logger) {
        // Clone or update?
        if (new File(repositoryDir, ".git").exists()) {
            // Fetch
            fetch(logger);
        } else {
            // Clone
            cloneRemote(logger);
        }
    }

    protected synchronized void fetch(Consumer<String> logger) {
        logger.accept(format("[git] Pulling %s into %s", repository.getRemote(), repositoryDir));
        try {
            git.fetch()
                    .setCredentialsProvider(credentialsProvider)
                    .call();
        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        }
        logger.accept(format("[git] Pulling done for %s", repository.getRemote()));
    }

    protected synchronized void cloneRemote(Consumer<String> logger) {
        logger.accept(format("[git] Cloning %s into %s", repository.getRemote(), repositoryDir));
        try {
            new CloneCommand()
                    .setCredentialsProvider(credentialsProvider)
                    .setDirectory(repositoryDir)
                    .setURI(repository.getRemote())
                    .call();
        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        }
        // Check
        if (!new File(repositoryDir, ".git").exists()) {
            throw new GitRepositoryCannotCloneException(repository.getRemote());
        }
        // Done
        logger.accept(format("[git] Clone done for %s", repository.getRemote()));
    }

    @Override
    public boolean isCompatible(GitRepository repository) {
        return Objects.equals(this.repository, repository);
    }
}
