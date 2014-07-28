package net.nemerosa.ontrack.extension.github.client;

import org.eclipse.egit.github.core.client.GitHubClient;

public interface GitHubClientConfigurator {

    void configure(GitHubClient client);

}
