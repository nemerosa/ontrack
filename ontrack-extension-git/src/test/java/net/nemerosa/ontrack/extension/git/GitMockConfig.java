package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.git.GitRepositoryClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Profile(GitMockConfig.PROFILE_GIT_MOCK)
public class GitMockConfig {

    public static final String PROFILE_GIT_MOCK = "git.mock";

    @Bean
    public GitRepositoryClient testGitRepositoryClient() {
        return mock(GitRepositoryClient.class);
    }

    @Bean
    @Primary
    public GitRepositoryClientFactory repositoryClientFactory() {
        GitRepositoryClientFactory factory = mock(GitRepositoryClientFactory.class);
        when(factory.getClient(any(GitRepository.class))).thenReturn(testGitRepositoryClient());
        return factory;
    }

}
