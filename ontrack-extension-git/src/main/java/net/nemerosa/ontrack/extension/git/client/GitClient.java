package net.nemerosa.ontrack.extension.git.client;

import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface GitClient {

    Collection<GitTag> getTags();

    GitConfiguration getConfiguration();

    /**
     * Gets a Git log between two boundaries.
     *
     * @param from Commitish string
     * @param to   Commitish string
     * @return Stream of commits
     */
    Stream<GitCommit> rawLog(String from, String to);

    /**
     * Gets a Git log between two boundaries.
     *
     * @param from Commitish string
     * @param to   Commitish string
     * @return Git log
     */
    GitLog log(String from, String to);

    GitCommit toCommit(RevCommit revCommit);

    void sync(Consumer<String> logger);

    GitDiff diff(String from, String to);

    GitCommit getCommitFor(String commit);

    /**
     * Gets the earliest commit that contains the commit.
     * <p>
     * Uses the <code>git tag --contains</code> command to get all tags that contains the given
     * {@code gitCommitId}.
     * <p>
     * <b>Note</b>: returned tags are <i>not</i> ordered.
     */
    Collection<String> getTagsWhichContainCommit(String gitCommitId);

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
