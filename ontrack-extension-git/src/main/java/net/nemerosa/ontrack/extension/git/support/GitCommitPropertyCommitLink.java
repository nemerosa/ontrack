package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.GitBranchConfiguration;
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty;
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.model.support.NoConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Build/commit link based on a {@link net.nemerosa.ontrack.extension.git.property.GitCommitProperty} property
 * set on the build.
 */
@Component
public class GitCommitPropertyCommitLink implements BuildGitCommitLink<NoConfig> {

    private final PropertyService propertyService;
    private final StructureService structureService;

    @Autowired
    public GitCommitPropertyCommitLink(PropertyService propertyService, StructureService structureService) {
        this.propertyService = propertyService;
        this.structureService = structureService;
    }

    @Override
    public String getId() {
        return "git-commit-property";
    }

    @Override
    public String getName() {
        return "Git Commit Property";
    }

    @Override
    public NoConfig clone(NoConfig data, Function<String, String> replacementFunction) {
        return data;
    }

    @Override
    public String getCommitFromBuild(Build build, NoConfig data) {
        return propertyService.getProperty(build, GitCommitPropertyType.class)
                .option()
                .map(GitCommitProperty::getCommit)
                .orElseThrow(() -> new NoGitCommitPropertyException(build.getEntityDisplayName()));
    }

    @Override
    public NoConfig parseData(JsonNode node) {
        return NoConfig.INSTANCE;
    }

    @Override
    public JsonNode toJson(NoConfig data) {
        return JsonUtils.object().end();
    }

    @Override
    public Form getForm() {
        return Form.create();
    }

    @Nullable
    @Override
    public Integer getEarliestBuildAfterCommit(Branch branch, GitRepositoryClient gitClient, GitBranchConfiguration branchConfiguration, NoConfig data, String commit) {
        return null;
    }

    @Override
    public Stream<String> getBuildCandidateReferences(String commit, Branch branch, GitRepositoryClient gitClient, GitBranchConfiguration branchConfiguration, NoConfig data) {
        if (gitClient.isCommit(commit)) {
            // Gets all commits
            return gitClient.log(
                    String.format("%s~1", commit),
                    gitClient.getBranchRef(branchConfiguration.getBranch())
            )
                    // Sorts the commits
                    .sorted()
                            // Looks for the builds which contains the given commit
                    .flatMap(gitCommit -> propertyService.searchWithPropertyValue(
                            GitCommitPropertyType.class,
                            ((entityType, id) -> entityType.getEntityFn(structureService).apply(id)),
                            gitCommitProperty -> StringUtils.equals(gitCommitProperty.getCommit(), gitCommit.getId())
                    ).stream())
                            // Filters on the branch
                    .filter(projectEntity -> ((Build) projectEntity).getBranch().getId().equals(branch.getId()))
                            // Gets the build names
                    .map(projectEntity -> ((Build) projectEntity).getName())
                            // Unique build names
                    .distinct();
        } else {
            return Collections.<String>emptyList().stream();
        }
    }

    @Override
    public boolean isBuildEligible(Build build, NoConfig data) {
        return propertyService.hasProperty(build, GitCommitPropertyType.class);
    }

    /**
     * No validation for build names.
     */
    @Override
    public boolean isBuildNameValid(String name, NoConfig data) {
        return true;
    }
}
