package net.nemerosa.ontrack.git;

import net.nemerosa.ontrack.git.model.*;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Defines a client for a Git repository.
 */
public interface GitRepositoryClient {

    /**
     * Tests the connection
     *
     * @throws net.nemerosa.ontrack.common.BaseException When the remote Git repository is not reachable
     */
    void test();

    /**
     * Makes sure the repository is synchronised with its remote location.
     *
     * @param logger Used to log messages during the synchronisation
     */
    void sync(Consumer<String> logger);

    /**
     * Checks if the given repository is compatible with this client. The remote, user name
     * and password must be checked.
     */
    boolean isCompatible(GitRepository repository);


    /**
     * Gets a Git log between two boundaries.
     *
     * @param from Commitish string
     * @param to   Commitish string
     * @return Stream of commits
     */
    Stream<GitCommit> log(String from, String to);

    /**
     * Gets a graph Git log between two boundaries.
     *
     * @param from Commitish string
     * @param to   Commitish string
     * @return Git log
     */
    GitLog graph(String from, String to);

    /**
     * Gets the full hash for a commit
     */
    String getId(RevCommit revCommit);

    /**
     * Gets the abbreviated hash for a commit
     */
    String getShortId(RevCommit revCommit);

    /**
     * Consolidation for a commit
     */
    GitCommit toCommit(RevCommit revCommit);


    /**
     * Scans the whole history.
     *
     * @param branch       Branch to follow
     * @param scanFunction Function that scans the commits. Returns <code>true</code> if the scan
     *                     must not go on, <code>false</code> otherwise.
     * @return <code>true</code> if at least one call to <code>scanFunction</code> has returned <code>true</code>.
     */
    boolean scanCommits(String branch, Predicate<RevCommit> scanFunction);

    /**
     * Gets the reference string for a branch given with its local name.
     */
    String getBranchRef(String branch);

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
     * Gets the list of remote branches, as defined under <code>ref/heads</code>.
     */
    List<String> getRemoteBranches();

    /**
     * Difference between two commit-ish boundaries
     */
    GitDiff diff(String from, String to);

    /**
     * Looks for a commit using its hash
     */
    Optional<GitCommit> getCommitFor(String id);

    /**
     * List of all tags
     */
    Collection<GitTag> getTags();

    /**
     * Checks if the {@code commitish} string can be parsed into this repository
     *
     * @param commitish Commitish string
     * @return <code>true</code> if this is a valid commit-like entry, <code>false</code> otherwise
     */
    boolean isCommit(String commitish);

    /**
     * Gets the unified diff between two boundaries, for a given list of paths
     *
     * @param from       Commitish from
     * @param to         Commitish to
     * @param pathFilter Filter on the path
     * @return Unified diff
     */
    String unifiedDiff(String from, String to, Predicate<String> pathFilter);

    /**
     * Downloads a document
     */
    Optional<String> download(String branch, String path);

    /**
     * Gets the synchronisation status
     */
    GitSynchronisationStatus getSynchronisationStatus();

    /**
     * Gets the list of all local branches, and their last commit. If the repository is not synched, or is currently
     * being synched, the map is returned empty.
     */
    GitBranchesInfo getBranches();

    /**
     * Resets the repository. Performs even if there is a synchronisation going on.
     */
    void reset();

    /**
     * Checks the log history and returns <code>true</code> if the token can be found.
     *
     * @param token Expression to be searched for
     * @return Result of the search
     */
    boolean isPatternFound(String token);
}
