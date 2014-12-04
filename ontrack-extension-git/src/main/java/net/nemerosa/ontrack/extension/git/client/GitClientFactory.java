package net.nemerosa.ontrack.extension.git.client;

import net.nemerosa.ontrack.extension.git.model.FormerGitConfiguration;

public interface GitClientFactory {

    GitClient getClient(FormerGitConfiguration gitConfiguration);

}
