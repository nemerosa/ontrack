package net.nemerosa.ontrack.shell

import org.kohsuke.args4j.Option
import org.kohsuke.args4j.spi.MapOptionHandler

class ShellOptions {

    @Option(name = "--url", usage = "Ontrack URL", required = true)
    String url

    @Option(name = "--user", aliases = "-u", usage = "Ontrack user")
    String user

    @Option(name = "--password", aliases = "-p", usage = "Ontrack password")
    String password

    @Option(name = "--file", aliases = "-f", usage = "Path to the script file or '-' if script is provided on the standard input (default)")
    String path = "-"

    @Option(name = "--value", aliases = "-v", usage = "Name and values to bind to the script, using name=value format", handler = MapOptionHandler)
    Map<String, String> values = [:]

    @Option(name = "--values", aliases = "-i", usage = "Path to a file which contains name/value pairs to inject into the script")
    File valueFile = null

    @Option(name = "--no-ssl", usage = "Disables SSL certificate checks")
    boolean disableSsl = false

    @Option(name = "--discard-result", aliases = "-d", usage = "If set, the result of the script is not written out, only the list of variables set by the script if any")
    boolean discardResult = false

}
