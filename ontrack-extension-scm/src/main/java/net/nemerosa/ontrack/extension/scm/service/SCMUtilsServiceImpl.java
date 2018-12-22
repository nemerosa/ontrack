package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFile;
import net.nemerosa.ontrack.extension.scm.model.SCMIssueCommitBranchInfo;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildView;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SCMUtilsServiceImpl implements SCMUtilsService {

    private final StructureService structureService;

    @Autowired
    public SCMUtilsServiceImpl(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    public <T extends SCMChangeLogFile> String diff(List<T> changeLogFiles, List<String> patterns, Function<T, String> diffFn) {
        // Predicate
        Predicate<String> pathFilter = getPathFilter(patterns);
        // Gets all changes
        return changeLogFiles.stream()
                // Filters on path
                .filter(changeLogFile -> pathFilter.test(changeLogFile.getPath()))
                // Collects the diffs
                .map(diffFn)
                // Group together
                .collect(Collectors.joining("\n"))
                ;
    }

    @Override
    public Predicate<String> getPathFilter(List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return path -> true;
        } else {
            return path -> patterns.stream()
                    .map(pattern ->
                            Pattern.compile(
                                    "^" +
                                            pattern
                                                    .replace("**", "$MULTI$")
                                                    .replace("*", "$SINGLE$")
                                                    .replace("$SINGLE$", "[^\\/]+")
                                                    .replace("$MULTI$", ".*") +
                                            "$"
                            )
                    ).anyMatch(
                            pattern -> pattern.matcher(path).matches()
                    );
        }
    }

    @Override
    public SCMIssueCommitBranchInfo getBranchInfo(@Nullable Build buildAfterCommit, SCMIssueCommitBranchInfo branchInfo) {
        SCMIssueCommitBranchInfo info = branchInfo;
        if (buildAfterCommit != null) {
            // Gets the build view
            BuildView buildView = structureService.getBuildView(buildAfterCommit, true);
            // Adds it to the list
            info = info.withBuildView(buildView);
            // Collects the promotions for the branch
            info = info.withBranchStatusView(
                    structureService.getEarliestPromotionsAfterBuild(buildAfterCommit)
            );
        }
        // OK
        return info;
    }

}
