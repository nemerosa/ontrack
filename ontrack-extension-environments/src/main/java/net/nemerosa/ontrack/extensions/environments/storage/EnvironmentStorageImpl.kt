package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EnvironmentStorageImpl(
    private val storageService: StorageService,
) : EnvironmentStorage {

    companion object {
        private const val STORE = "Environment"
    }

    override fun save(env: Environment): Environment {
        storageService.store(
            store = STORE,
            key = env.id,
            data = env,
        )
        return env
    }

    override fun getById(id: String): Environment = storageService.find(
        store = STORE,
        key = id,
        type = Environment::class
    ) ?: throw EnvironmentIdNotFoundException(id)

    override fun findByName(name: String): Environment? =
        storageService.filter(
            store = STORE,
            type = Environment::class,
            query = "data::jsonb->>'name' = :name",
            queryVariables = mapOf("name" to name)
        ).firstOrNull()

    override fun findEnvironments(): List<Environment> =
        storageService.filter(
            store = STORE,
            type = Environment::class,
            size = Int.MAX_VALUE,
            orderQuery = "ORDER BY data::jsonb->>'order'"
        )
}