package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.extension.environments.Environment

interface EnvironmentRepositoryAccessor {

    fun getEnvironmentById(id: String): Environment

}