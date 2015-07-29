package net.nemerosa.ontrack.extension.stash.property;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.model.support.UserPassword;

import java.util.Optional;

import static java.lang.String.format;

public class StashGitConfiguration implements GitConfiguration {

    private final StashProjectConfigurationProperty property;

    public StashGitConfiguration(StashProjectConfigurationProperty property) {
        this.property = property;
    }

    @Override
    public String getType() {
        return "stash";
    }

    @Override
    public String getName() {
        return property.getConfiguration().getName();
    }

    @Override
    public String getRemote() {
        return format(
                "%s/scm/%s/%s.git",
                property.getConfiguration().getUrl(),
                property.getProject(),
                property.getRepository()
        );
    }

    @Override
    public Optional<UserPassword> getCredentials() {
        return property.getConfiguration().getCredentials();
    }

    @Override
    public String getCommitLink() {
        return format(
                "%s/projects/%s/repos/%s/commits/{commit}",
                property.getConfiguration().getUrl(),
                property.getProject(),
                property.getRepository()
        );
    }

    @Override
    public String getFileAtCommitLink() {
        return format(
                "%s/projects/%s/repos/%s/browse/{path}?at={commit",
                property.getConfiguration().getUrl(),
                property.getProject(),
                property.getRepository()
        );
    }

    @Override
    public int getIndexationInterval() {
        // FIXME Method net.nemerosa.ontrack.extension.stash.property.StashGitConfiguration.getIndexationInterval
        return 30;
    }

    @Override
    public String getIssueServiceConfigurationIdentifier() {
        // FIXME Method net.nemerosa.ontrack.extension.stash.property.StashGitConfiguration.getIssueServiceConfigurationIdentifier
        return null;
    }
}
