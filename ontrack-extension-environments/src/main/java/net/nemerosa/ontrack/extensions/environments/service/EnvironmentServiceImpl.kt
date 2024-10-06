package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.storage.EnvironmentNameAlreadyExists
import net.nemerosa.ontrack.extensions.environments.storage.EnvironmentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EnvironmentServiceImpl(
    private val environmentRepository: EnvironmentRepository,
) : EnvironmentService {

    override fun save(environment: Environment) {
        // TODO Security check
        val existing = environmentRepository.findByName(environment.name)
        if (existing != null && existing.id != environment.id) {
            throw EnvironmentNameAlreadyExists(environment.name)
        }
        environmentRepository.save(environment)
    }

    override fun getById(id: String): Environment {
        // TODO Security check
        return environmentRepository.getEnvironmentById(id)
    }

    override fun findByName(name: String): Environment? {
        // TODO Security check
        return environmentRepository.findByName(name)
    }

    override fun findAll(): List<Environment> {
        // TODO Security check
        return environmentRepository.findAll()
    }

    override fun delete(env: Environment) {
        // TODO Security check
        environmentRepository.delete(env)
    }
}