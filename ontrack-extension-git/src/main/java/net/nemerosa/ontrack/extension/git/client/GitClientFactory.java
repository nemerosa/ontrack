package net.nemerosa.ontrack.extension.git.client;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;

public interface GitClientFactory {

    GitClient getClient(GitConfiguration gitConfiguration);

}
