package net.nemerosa.ontrack.extension.git.config;

import net.nemerosa.ontrack.git.GitRepositoryClientFactory;
import net.nemerosa.ontrack.git.support.GitRepositoryClientFactoryImpl;
import net.nemerosa.ontrack.model.support.EnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class GitConfig {

    @Autowired
    private EnvService envService;

    @Bean
    public GitRepositoryClientFactory gitRepositoryClientFactory() {
        File repositories = envService.getWorkingDir("git", "repositories");
        return new GitRepositoryClientFactoryImpl(repositories);
    }

}
