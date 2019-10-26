package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFile;
import net.nemerosa.ontrack.extension.scm.model.SCMIssueCommitBranchInfo;
import net.nemerosa.ontrack.model.structure.Build;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generic service around the SCM
 */
public interface SCMUtilsService {

    <T extends SCMChangeLogFile> String diff(List<T> changeLogFiles, List<String> patterns, Function<T, String> diffFn);

    Predicate<String> getPathFilter(List<String> patterns);

    /**
     * Completes information about a branch by extracting additional
     * information from a build
     */
    SCMIssueCommitBranchInfo getBranchInfo(@Nullable Build buildAfterCommit, SCMIssueCommitBranchInfo branchInfo);
}
