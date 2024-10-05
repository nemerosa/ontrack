package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment

interface EnvironmentStorage {

    fun save(env: Environment): Environment
    fun getById(id: String): Environment
    fun findByName(name: String): Environment?
    fun findEnvironments(): List<Environment>

}