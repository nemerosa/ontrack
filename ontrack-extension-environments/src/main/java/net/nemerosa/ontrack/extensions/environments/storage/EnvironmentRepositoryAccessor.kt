package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment

interface EnvironmentRepositoryAccessor {

    fun getEnvironmentById(id: String): Environment

}