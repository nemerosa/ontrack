package net.nemerosa.ontrack.shell

import groovy.json.JsonOutput
import net.nemerosa.ontrack.dsl.v4.DSLException
import net.nemerosa.ontrack.dsl.v4.Ontrack
import net.nemerosa.ontrack.dsl.v4.OntrackConnection
import net.nemerosa.ontrack.dsl.v4.OntrackLogger
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser

class Shell {

    private final PrintWriter output
    private final OntrackLogger logger
    private final boolean cmdLine

    Shell(PrintWriter output, boolean cmdLine = false, OntrackLogger logger = null) {
        this.output = output
        this.cmdLine = cmdLine
        this.logger = logger
    }

    Shell(boolean cmdLine = false, OntrackLogger logger = null) {
        this(new PrintWriter(System.out), cmdLine, logger)
    }

    static Shell forCmdLine(OntrackLogger logger = null) {
        new Shell(true, logger)
    }

    static Shell withOutput(Writer writer) {
        new Shell(new PrintWriter(writer))
    }

    def call(String... args) {
        call(args as List)
    }

    def call(List<String> args) {
        // Parsing of options
        def options = new ShellOptions()
        def parser = new CmdLineParser(options)
        // Help?
        if (cmdLine && (args.contains("--help") || args.contains("-h"))) {
            parser.printUsage(System.out)
        } else {
            // Parsing
            try {
                parser.parseArgument(args)
            } catch (CmdLineException ex) {
                if (cmdLine) {
                    System.err.println ex.message
                    parser.printUsage(System.out)
                    System.exit(-1)
                } else {
                    throw ex
                }
            }
            // Call
            try {
                call options
            } catch (DSLException ex) {
                if (cmdLine) {
                    System.err.println ex.message
                    System.exit(1)
                } else {
                    throw ex
                }
            }
        }
    }

    def call(ShellOptions options) {

        // Creates the Ontrack connector
        def connection = OntrackConnection.create(options.url).disableSsl(options.disableSsl)
        if (options.user) {
            connection = connection.authenticate(options.user, options.password)
        }
        if (logger) {
            connection = connection.logger(logger)
        }
        def ontrack = connection.build()

        // Creates the local shell connector
        def local = new ShellConnector()

        // Reads the script
        def script = readScript(options)

        // Parsing of values
        def params = options.values

        // Values in file
        if (options.valueFile) {
            Properties properties = new Properties()
            options.valueFile.withInputStream {
                properties.load(it)
            }
            params.putAll(properties as Map)
        }

        // Runs the shell
        def result = run(ontrack, local, script, params)

        // Output values
        if (options.discardResult) {
            local.values.each { k, v ->
                output.format('%s=%s%n', k, v)
            }
            output.flush()
        } else {
            // Output of result
            def json = JsonOutput.prettyPrint(JsonOutput.toJson(result))
            output.println(json)
            output.flush()
        }
    }

    public static String readScript(ShellOptions options) {
        if (options.path == '-') {
            return System.in.text
        } else {
            return new File(options.path).text
        }
    }

    static def run(Ontrack ontrack, ShellConnector connector, String script, Map<String, String> params) {
        // Binding
        //noinspection GroovyAssignabilityCheck
        Map<String, ?> bindingMap = [
                ontrack: ontrack,
                shell  : connector
        ] + params
        def binding = new Binding(bindingMap);

        // Shell
        def gshell = new GroovyShell(binding)

        // Running the script
        gshell.evaluate(script)
    }

    static void main(String... args) {
        forCmdLine()(args)
    }

}
