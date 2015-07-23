package net.nemerosa.ontrack.gradle

import org.gradle.api.DefaultTask
/**
 * Base class
 */
abstract class AbstractCDTask extends DefaultTask {

    /**
     * Utility method to get a string from running a process
     */
    String execute(String program, List arguments) {
        return new ByteArrayOutputStream().withStream { os ->
            project.exec {
                executable = program
                args = arguments
                standardOutput = os
            }
            return os.toString().trim()
        }
    }

    /**
     * Utility method to get a string from running a process
     */
    String execute(String program, Object... arguments) {
        return execute(program, arguments as List)
    }

}
