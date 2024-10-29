package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.EnvironmentFilter

interface EnvironmentService {

    /**
     * Saving a new or existing environment
     */
    fun save(environment: Environment)

    /**
     * Getting an environment using its ID
     */
    fun getById(id: String): Environment

    /**
     * Getting an environment using its name
     */
    fun findByName(name: String): Environment?

    /**
     * Gets a list of environments.
     */
    fun findAll(
        filter: EnvironmentFilter = EnvironmentFilter(),
    ): List<Environment>

    /**
     * Deletes an environment
     */
    fun delete(env: Environment)

}