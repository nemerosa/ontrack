package net.nemerosa.ontrack.dsl

import org.kohsuke.args4j.Option

class ShellOptions {

    @Option(name = "--url", usage = "Ontrack URL", required = true)
    String url

    @Option(name = "--user", aliases = "-u", usage = "Ontrack user")
    String user

    @Option(name = "--password", aliases = "-p", usage = "Ontrack password")
    String password

    @Option(name = "--file", aliases = "-f", usage = "Path to the script file or '-' if script is provided on the standard input (default)")
    String path = "-"

    @Option(name = "--value", aliases = "-v", usage = "Name/value to bind to the script, using name=value format")
    List<String> values = []

    @Option(name = "--no-ssl", usage = "Disables SSL certificate checks")
    boolean disableSsl = false

}
