package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.BuildDiffExtension;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.model.structure.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitChangeLogExtension extends AbstractExtension implements BuildDiffExtension {

    private final GitService gitService;

    @Autowired
    public GitChangeLogExtension(GitExtensionFeature extensionFeature, GitService gitService) {
        super(extensionFeature);
        this.gitService = gitService;
    }

    @Override
    public Action getAction() {
        return Action.of("git-changelog", "Change log", "changelog");
    }

    /**
     * Checks that the branch is properly configured with a Git configuration.
     */
    @Override
    public boolean apply(Branch branch) {
        return gitService.isBranchConfiguredForGit(branch);
    }
}
