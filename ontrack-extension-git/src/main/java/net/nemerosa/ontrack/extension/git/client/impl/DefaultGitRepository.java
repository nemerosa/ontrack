package net.nemerosa.ontrack.extension.git.client.impl;

import net.nemerosa.ontrack.model.support.UserPassword;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.format;

public class DefaultGitRepository implements GitRepository {

    private final File wd;
    private final String remote;
    private final String branch;
    private final String id;
    private final CredentialsProvider credentialsProvider;
    private Git git;

    public DefaultGitRepository(File wd, String remote, String branch, String id, Supplier<Optional<UserPassword>> userPasswordSupplier) {
        this.wd = wd;
        this.remote = remote;
        this.branch = branch;
        this.id = id;
        // Credentials
        this.credentialsProvider = userPasswordSupplier.get()
                .map(up -> new UsernamePasswordCredentialsProvider(up.getUser(), up.getPassword()))
                .orElse(null);
    }

    @Override
    public File wd() {
        return wd;
    }

    @Override
    public String getRemote() {
        return remote;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getBranch() {
        return branch;
    }

    @Override
    public synchronized void sync(Consumer<String> logger) throws GitAPIException {
        // Clone or update?
        if (new File(wd, ".git").exists()) {
            // Fetch
            pull(logger);
        } else {
            // Clone
            cloneRemote(logger);
        }
    }

    @Override
    public synchronized Git git() {
        if (git == null) {
            // Gets the repository
            Repository repository;
            try {
                repository = new FileRepositoryBuilder()
                        .setWorkTree(wd)
                        .build();
            } catch (IOException e) {
                throw new GitException(e);
            }
            // Gets the Git
            git = new Git(repository);
        }
        return git;
    }

    @Override
    public RevCommit getCommitForTag(Ref tag) {
        try {
            Repository repo = git().getRepository();
            RevWalk walk = new RevWalk(repo);
            try {
                return walk.parseCommit(tag.getObjectId());
            } finally {
                walk.release();
            }
        } catch (IOException e) {
            throw new GitException(e);
        }
    }

    protected synchronized void cloneRemote(Consumer<String> logger) throws GitAPIException {
        logger.accept(format("[git] Cloning %s into %s", remote, wd));
        new CloneCommand()
                .setCredentialsProvider(credentialsProvider)
                .setDirectory(wd)
                .setURI(remote)
                .setBranch(branch)
                .setBranchesToClone(Collections.singleton(branch))
                .call();
        // Check
        if (!new File(wd, ".git").exists()) {
            throw new GitCannotCloneException(wd);
        }
        // Done
        logger.accept(format("[git] Clone done for %s", remote));
    }

    protected synchronized void pull(Consumer<String> logger) throws GitAPIException {
        logger.accept(format("[git] Pulling %s into %s", remote, wd));
        git().pull().setCredentialsProvider(credentialsProvider).call();
        logger.accept(format("[git] Pulling done for %s", remote));
    }

}
