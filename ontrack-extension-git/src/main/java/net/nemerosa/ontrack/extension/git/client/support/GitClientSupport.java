package net.nemerosa.ontrack.extension.git.client.support;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     *
     * @param wd       Repository directory
     * @param commitId Commit to search
     * @return List of tag names
     */
    public static List<String> tagContains(File wd, String commitId) {
        return asList(run(wd, "git", "tag", "--contains", commitId));
    }

    /**
     * Splits the output in several lines.
     * <p>
     * TODO This code should be moved in a utility class in <code>ontrack-common</code>.
     *
     * @param output Output received from the command.
     * @return Lines. This can be empty but not null.
     */
    private static List<String> asList(String output) {
        try {
            return IOUtils.readLines(new StringReader(output));
        } catch (IOException e) {
            throw new RuntimeException("Cannot get lines", e);
        }
    }

    /**
     * Runs a command in the <code>wd</code> directory and returns its output. In case of error (exit
     * code different than 0), an exception is thrown.
     * <p>
     * TODO This code should be moved in a utility class in <code>ontrack-common</code>.
     *
     * @param wd   Directory where to execute the command
     * @param cmd  Command to execute
     * @param args Command parameters
     * @return Output of the command
     */
    private static String run(File wd, String cmd, String... args) {
        // Complete list of arguments
        List<String> list = new ArrayList<>();
        list.add(cmd);
        list.addAll(Arrays.asList(args));
        try {
            // Builds a process
            Process process = new ProcessBuilder(list).directory(wd).start();
            // Running the process and waiting for its completion
            int exit = process.waitFor();
            // In case of error
            if (exit != 0) {
                String error = IOUtils.toString(process.getErrorStream());
                throw new RuntimeException(error);
            } else {
                return IOUtils.toString(process.getInputStream());
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Error while executing " + cmd + " command", ex);
        }
    }

}
