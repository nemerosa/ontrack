package net.nemerosa.ontrack.gradle

import org.gradle.api.DefaultTask
/**
 * Base class
 */
abstract class AbstractCDTask extends DefaultTask {

    /**
     * Directory where to run the command
     */
    File dir

    /**
     * Utility method to get a string from running a process
     */
    String execute(Map<String, ?> env, String program, List arguments) {
        println "[${name}] ${program} ${arguments}"
        if (dir) println "[${name}] DIR ${dir}"
        env.each { key, value ->
            println "[${name}] ENV ${key} ${value}"
        }
        return new ByteArrayOutputStream().withStream { os ->
            project.exec {
                executable = program
                args = arguments
                standardOutput = os
                if (dir) workingDir = dir
                environment(env)
            }
            return os.toString().trim()
        }
    }

    /**
     * Utility method to get a string from running a process
     */
    String execute(String program, List arguments) {
        return execute([:], program, arguments)
    }

    /**
     * Utility method to get a string from running a process
     */
    String execute(String program, Object... arguments) {
        return execute(program, arguments as List)
    }

}
