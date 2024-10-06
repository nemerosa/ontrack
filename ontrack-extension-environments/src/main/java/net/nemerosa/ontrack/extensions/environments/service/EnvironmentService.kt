package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.extensions.environments.Environment

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
    fun findAll(): List<Environment>

    /**
     * Deletes an environment
     */
    fun delete(env: Environment)

}