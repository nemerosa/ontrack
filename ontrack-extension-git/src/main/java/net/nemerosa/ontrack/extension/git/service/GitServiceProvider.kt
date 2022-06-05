package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.scm.service.SCMService;
import net.nemerosa.ontrack.extension.scm.service.SCMServiceProvider;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GitServiceProvider implements SCMServiceProvider {

    private final GitService gitService;

    @Autowired
    public GitServiceProvider(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public Optional<SCMService> getScmService(Branch branch) {
        return getScmService(branch.getProject());
    }

    @Override
    public Optional<SCMService> getScmService(Project project) {
        GitConfiguration projectConfiguration = gitService.getProjectConfiguration(project);
        if (projectConfiguration != null) {
            return Optional.of(gitService);
        } else {
            return Optional.empty();
        }
    }
}
