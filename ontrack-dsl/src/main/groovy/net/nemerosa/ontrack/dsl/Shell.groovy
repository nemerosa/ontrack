package net.nemerosa.ontrack.dsl

import org.kohsuke.args4j.CmdLineParser

class Shell {

    private final PrintWriter output

    Shell(PrintWriter output) {
        this.output = output
    }

    Shell() {
        this(new PrintWriter(System.out))
    }

    Shell withOutput(Writer writer) {
        new Shell(new PrintWriter(writer))
    }

    def call(String... args) {
        // Parsing of options
        def options = new ShellOptions()
        new CmdLineParser(options).parseArgument(args)
        // Call
        call options
    }

    def call(ShellOptions options) {

        // Creates the Ontrack connector
        def connection = OntrackConnection.create(options.url)
        // TODO SSL support
        if (options.user) {
            connection = connection.authenticate(options.user, options.password)
        }
        def ontrack = connection.build()

        // Creates the local shell connector
        def local = new ShellConnector()

        // Reads the script
        def script = new File(options.path).text

        // Runs the shell
        run ontrack, local, script

        // Output values
        local.values.each { k, v ->
            output.format('%s=%s%n', k, v)
        }

    }

    static def run(Ontrack ontrack, ShellConnector connector, String script) {
        // Binding
        def binding = new Binding([
                ontrack: ontrack,
                shell  : connector
                // TODO Other parameters
        ]);

        // Shell
        def gshell = new GroovyShell(binding)

        // Running the script
        gshell.evaluate(script)
    }

    static Shell create() {
        new Shell()
    }

    static void main(String... args) {
        create()(args)
    }

}
