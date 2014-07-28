package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.model.structure.Branch;

public interface GitHubClientConfiguratorFactory {

    GitHubClientConfigurator getGitHubConfigurator(Branch branch);

}
