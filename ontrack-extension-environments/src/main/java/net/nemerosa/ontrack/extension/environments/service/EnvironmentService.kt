package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.common.Document
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

    /**
     * Sets an image for an environment
     */
    fun setEnvironmentImage(id: String, document: Document)

    /**
     * Downloads the image for an environment.
     *
     * Returns an empty document if image is not available.
     */
    fun getEnvironmentImage(id: String): Document

}