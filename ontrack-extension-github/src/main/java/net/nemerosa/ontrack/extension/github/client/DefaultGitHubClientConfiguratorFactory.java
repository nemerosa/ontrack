package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitHubClientConfiguratorFactory implements GitHubClientConfiguratorFactory {

    @Override
    public GitHubClientConfigurator getGitHubConfigurator(GitHubConfiguration configuration) {
        return client -> {
            String oAuth2Token = configuration.getOAuth2Token();
            if (StringUtils.isNotBlank(oAuth2Token)) {
                client.setOAuth2Token(oAuth2Token);
            } else {
                String user = configuration.getUser();
                String password = configuration.getPassword();
                if (StringUtils.isNotBlank(user)) {
                    client.setCredentials(user, password);
                }
            }
        };
    }
}
