package net.nemerosa.ontrack.extension.casc

/**
 * Runs some Configuration as Code.
 */
interface CascService {

    /**
     * Runs CasC from a series of YAML texts
     *
     * @param yaml List of YAML content
     */
    fun runYaml(vararg yaml: String) = runYaml(yaml.toList())

    /**
     * Runs CasC from a series of YAML texts
     *
     * @param yaml List of YAML content
     */
    fun runYaml(yaml: List<String>)
}