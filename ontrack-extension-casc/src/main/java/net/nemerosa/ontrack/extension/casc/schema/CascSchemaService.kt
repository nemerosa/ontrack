package net.nemerosa.ontrack.extension.casc.schema

interface CascSchemaService {

    /**
     * The schema
     */
    val schema: CascType

    /**
     * List of CasC resources
     */
    val locations: List<String>

}