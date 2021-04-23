package net.nemerosa.ontrack.extension.casc

/**
 * Running the CasC files.
 */
interface CascLoadingService {

    /**
     * Gets the list of CasC files from the [configuration][CascConfigurationProperties], parses them and runs the configuration.
     */
    fun load()

    /**
     * Parses the CasC locations and runs the configuration.
     */
    fun load(locations: List<String>)

}