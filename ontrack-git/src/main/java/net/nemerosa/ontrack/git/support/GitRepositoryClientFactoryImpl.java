package net.nemerosa.ontrack.git.support;

import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.git.GitRepositoryClientFactory;

import java.io.File;

public class GitRepositoryClientFactoryImpl implements GitRepositoryClientFactory {

    private final File root;

    public GitRepositoryClientFactoryImpl(File root) {
        this.root = root;
    }

    @Override
    public GitRepositoryClient getClient(GitRepository repository) {
        // FIXME Method net.nemerosa.ontrack.git.support.GitRepositoryClientFactoryImpl.getClient
        return null;
    }

}
