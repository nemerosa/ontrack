package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.support.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GitProjectSyncActionExtension extends AbstractExtension implements ProjectEntityActionExtension {

    private final GitService gitService;
    private final SecurityService securityService;

    @Autowired
    public GitProjectSyncActionExtension(
            GitExtensionFeature extensionFeature,
            GitService gitService, SecurityService securityService) {
        super(extensionFeature);
        this.gitService = gitService;
        this.securityService = securityService;
    }

    @Override
    public Optional<Action> getAction(ProjectEntity entity) {
        if (entity instanceof Project && securityService.isProjectFunctionGranted(entity, ProjectConfig.class)) {
            GitConfiguration projectConfiguration = gitService.getProjectConfiguration((Project) entity);
            if (projectConfiguration != null) {
                return Optional.of(
                        Action.of(
                                "git-project-sync",
                                "Force Git project sync",
                                String.format("project-sync/%d", entity.id())
                        )
                );
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

}
