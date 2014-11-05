package net.nemerosa.ontrack.extension.git.client.support;

import net.nemerosa.ontrack.common.ProcessExitException;
import net.nemerosa.ontrack.common.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Optional;

/**
 * Utility classes that use the Git command line when the JGit client is not enough.
 */
public class GitClientSupport {

    /**
     * Returns the first tag a commit belongs to.
     * <p>
     * Same as:
     * <pre>
     *     git describe --contains $commitId --abbrev=0
     * </pre>
     *
     * @param wd       Repository directory
     * @param commitId Commit to search
     * @return Tag name
     */
    public static Optional<String> tagContains(File wd, String commitId) {
        try {
            String output = Utils.run(wd, "git", "describe", "--contains", commitId, "--abbrev=0");
            if (StringUtils.isNotBlank(output)) {
                return Optional.of(output.trim().replaceAll("([^~]*)(~\\d+)?$", "$1"));
            } else {
                return Optional.empty();
            }
        } catch (ProcessExitException ex) {
            if (ex.getExit() == 128) {
                return Optional.empty();
            } else {
                throw ex;
            }
        }
    }

}
