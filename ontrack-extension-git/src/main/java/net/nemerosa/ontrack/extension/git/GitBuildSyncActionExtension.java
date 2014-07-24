package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.security.Action;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GitBuildSyncActionExtension extends AbstractExtension implements ProjectEntityActionExtension {

    private final GitService gitService;
    private final SecurityService securityService;

    @Autowired
    public GitBuildSyncActionExtension(
            GitExtensionFeature extensionFeature,
            GitService gitService, SecurityService securityService) {
        super(extensionFeature);
        this.gitService = gitService;
        this.securityService = securityService;
    }

    @Override
    public Optional<Action> getAction(ProjectEntity entity) {
        if (entity instanceof Branch
                && gitService.isBranchConfiguredForGit((Branch) entity)
                && securityService.isProjectFunctionGranted(entity, BuildCreate.class)) {
            return Optional.of(Action.of(
                    "git-sync",
                    "Git <-> Build sync",
                    String.format("sync/%d", entity.id())
            ));
        } else {
            return Optional.empty();
        }
    }

}
