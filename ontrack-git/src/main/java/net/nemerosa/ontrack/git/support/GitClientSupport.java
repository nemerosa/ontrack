package net.nemerosa.ontrack.git.support;

import net.nemerosa.ontrack.common.ProcessExitException;
import net.nemerosa.ontrack.common.Utils;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static net.nemerosa.ontrack.common.Utils.run;

/**
 * Utility classes that use the Git command line when the JGit client is not enough.
 */
public class GitClientSupport {

    /**
     * Returns the list of tags a commit belongs to.
     * <p>
     * Same as:
     * <pre>
     *     git tag --contains $commitId
     * </pre>
     * <p>
     * <b>Note</b>: the returned list of tags is <i>not</i> ordered.
     *
     * @param wd       Repository directory
     * @param commitId Commit to search
     * @return List of tag names
     */
    public static List<String> tagContains(File wd, String commitId) {
        return Utils.asList(run(wd, "git", "tag", "--contains", commitId));
    }

    /**
     * Output of a file
     * Same as:
     * <pre>
     * git show $branch:$path
     * </pre>
     *
     * @param wd       Repository directory
     * @param commitId Commit-ish to search
     * @param path     Path to download
     * @return Content of the file as text
     */
    public static Optional<String> showPath(File wd, String commitId, String path) {
        try {
            // Reads the file as bytes
            return Optional.of(
                    Utils.run(wd, "git", "show", String.format("%s:%s", commitId, path))
            );
        } catch (ProcessExitException ex) {
            if (ex.getExit() == 128) {
                return Optional.empty();
            } else {
                throw ex;
            }
        }
    }

}
