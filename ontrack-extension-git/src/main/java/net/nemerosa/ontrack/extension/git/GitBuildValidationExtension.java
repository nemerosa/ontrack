package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.BuildValidationExtension;
import net.nemerosa.ontrack.extension.api.model.BuildValidationException;
import net.nemerosa.ontrack.extension.git.model.GitBranchConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.Build;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitBuildValidationExtension extends AbstractExtension implements BuildValidationExtension {

    private final GitService gitService;

    @Autowired
    public GitBuildValidationExtension(GitExtensionFeature extensionFeature, GitService gitService) {
        super(extensionFeature);
        this.gitService = gitService;
    }

    @Override
    public void validateBuild(Build build) throws BuildValidationException {
        // Gets the Git branch configuration
        GitBranchConfiguration branchConfiguration = gitService.getBranchConfiguration(build.getBranch());
        if (branchConfiguration != null) {
            if (!branchConfiguration.getBuildCommitLink().isBuildNameValid(build.getName())) {
                throw new BuildValidationException(String.format(
                        "Build name %s is not valid for the branch Git configuration",
                        build.getName()
                ));
            }
        }
    }
}
