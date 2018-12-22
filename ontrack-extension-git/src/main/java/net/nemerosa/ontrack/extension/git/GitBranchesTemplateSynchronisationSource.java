package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.support.AbstractTemplateSynchronisationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class GitBranchesTemplateSynchronisationSource extends AbstractTemplateSynchronisationSource<GitBranchesTemplateSynchronisationSourceConfig> {

    private final GitService gitService;

    @Autowired
    public GitBranchesTemplateSynchronisationSource(GitService gitService) {
        super(GitBranchesTemplateSynchronisationSourceConfig.class);
        this.gitService = gitService;
    }

    @Override
    public String getId() {
        return "git-branches";
    }

    @Override
    public String getName() {
        return "Git branches";
    }

    @Override
    public boolean isApplicable(Branch branch) {
        return gitService.isBranchConfiguredForGit(branch);
    }

    @Override
    public Form getForm(Branch branch) {
        return GitBranchesTemplateSynchronisationSourceConfig.form();
    }

    @Override
    public List<String> getBranchNames(Branch branch, GitBranchesTemplateSynchronisationSourceConfig config) {
        GitConfiguration projectConfiguration = gitService.getProjectConfiguration(branch.getProject());
        if (projectConfiguration != null) {
            // Inclusion predicate
            Predicate<String> filter = config.getFilter();
            // Gets the list of branches
            return gitService.getRemoteBranches(projectConfiguration).stream()
                    .filter(filter)
                    .sorted()
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
