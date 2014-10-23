package net.nemerosa.ontrack.extension.git.client;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface GitClient {

    Collection<GitTag> getTags();

    GitConfiguration getConfiguration();

    GitLog log(String from, String to);

    GitCommit toCommit(RevCommit revCommit);

    void sync(Consumer<String> logger);

    GitDiff diff(String from, String to);

    boolean isCommitDefined(String commit);

    GitCommit getCommitFor(String commit);

    String getEarliestTagForCommit(String gitCommitId, Predicate<String> tagNamePredicate);

    /**
     * Scans the whole history.
     *
     * @param scanFunction Function that scans the commits. Returns <code>true</code> if the scan
     *                     must not go on, <code>false</code> otherwise.
     * @return <code>true</code> if at least one call to <code>scanFunction</code> has returned <code>true</code>.
     */
    boolean scanCommits(Predicate<RevCommit> scanFunction);

    /**
     * Gets the list of remote branches, as defined under <code>ref/heads</code>.
     */
    List<String> getRemoteBranches();
}
