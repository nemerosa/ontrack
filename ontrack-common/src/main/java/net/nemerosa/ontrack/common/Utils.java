package net.nemerosa.ontrack.common;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Utils {

    private Utils() {
    }

    /**
     * Splits a text in several lines.
     *
     * @param text Text to split
     * @return Lines. This can be empty but not null.
     */
    public static List<String> asList(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        } else {
            try {
                return IOUtils.readLines(new StringReader(text));
            } catch (IOException e) {
                throw new RuntimeException("Cannot get lines", e);
            }
        }
    }

    /**
     * Runs a command in the <code>wd</code> directory and returns its output. In case of error (exit
     * code different than 0), an exception is thrown.
     *
     * @param wd   Directory where to execute the command
     * @param cmd  Command to execute
     * @param args Command parameters
     * @return Output of the command
     */
    public static String run(File wd, String cmd, String... args) {
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

    /**
     * Writes some bytes in Hexadecimal format
     */
    public static String toHexString(byte[] bytes) {
        return toHexString(bytes, 0, bytes.length);
    }

    /**
     * Writes some bytes in Hexadecimal format
     */
    public static String toHexString(byte[] data, int start, int len) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int b = data[start + i] & 0xFF;
            if (b < 16) buf.append('0');
            buf.append(Integer.toHexString(b));
        }
        return buf.toString();
    }
}
