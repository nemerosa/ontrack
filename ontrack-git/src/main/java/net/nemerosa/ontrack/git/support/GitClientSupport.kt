package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.common.ProcessExitException
import net.nemerosa.ontrack.common.Utils
import net.nemerosa.ontrack.common.Utils.run
import java.io.File

/**
 * Utility classes that use the Git command line when the JGit client is not enough.
 */
object GitClientSupport {

    /**
     * Returns the list of tags a commit belongs to.
     *
     *
     * Same as:
     * <pre>
     * git tag --contains $commitId
    </pre> *
     *
     *
     * **Note**: the returned list of tags is *not* ordered.
     *
     * @param wd       Repository directory
     * @param commitId Commit to search
     * @return List of tag names
     */
    fun tagContains(wd: File, commitId: String): List<String> {
        return Utils.asList(run(wd, "git", "tag", "--contains", commitId))
    }

    /**
     * Output of a file
     * Same as:
     * <pre>
     * git show $branch:$path
    </pre> *
     *
     * @param wd       Repository directory
     * @param commitId Commit-ish to search
     * @param path     Path to download
     * @return Content of the file as text or `null` if not found
     */
    fun showPath(wd: File, commitId: String, path: String): String? {
        return try {
            // Reads the file as bytes
            Utils.run(wd, "git", "show", String.format("%s:%s", commitId, path))
        } catch (ex: ProcessExitException) {
            if (ex.exit == 128) {
                null
            } else {
                throw ex
            }
        }

    }

}
