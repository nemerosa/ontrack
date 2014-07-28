package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;

public interface GitHubClientConfiguratorFactory {

    GitHubClientConfigurator getGitHubConfigurator(GitHubConfiguration configuration);

}
