package net.nemerosa.ontrack.extension.casc

/**
 * Extension responsible to load some Casc fragments.
 */
interface CascLoader {

    /**
     * Loads a list of YAML documents.
     *
     * @return List of YAML documents.
     */
    fun loadCascFragments(): List<String>
}