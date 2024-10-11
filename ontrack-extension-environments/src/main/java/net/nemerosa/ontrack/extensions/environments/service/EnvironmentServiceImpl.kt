package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.EnvironmentFilter
import net.nemerosa.ontrack.extensions.environments.security.EnvironmentDelete
import net.nemerosa.ontrack.extensions.environments.security.EnvironmentList
import net.nemerosa.ontrack.extensions.environments.security.EnvironmentSave
import net.nemerosa.ontrack.extensions.environments.storage.EnvironmentNameAlreadyExists
import net.nemerosa.ontrack.extensions.environments.storage.EnvironmentRepository
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EnvironmentServiceImpl(
    private val environmentRepository: EnvironmentRepository,
    private val securityService: SecurityService,
) : EnvironmentService {

    override fun save(environment: Environment) {
        securityService.checkGlobalFunction(EnvironmentSave::class.java)
        val existing = environmentRepository.findByName(environment.name)
        if (existing != null && existing.id != environment.id) {
            throw EnvironmentNameAlreadyExists(environment.name)
        }
        environmentRepository.save(environment)
    }

    override fun getById(id: String): Environment {
        securityService.checkGlobalFunction(EnvironmentList::class.java)
        return environmentRepository.getEnvironmentById(id)
    }

    override fun findByName(name: String): Environment? {
        securityService.checkGlobalFunction(EnvironmentList::class.java)
        return environmentRepository.findByName(name)
    }

    override fun findAll(
        filter: EnvironmentFilter,
    ): List<Environment> {
        securityService.checkGlobalFunction(EnvironmentList::class.java)
        return environmentRepository.findAll(filter)
    }

    override fun delete(env: Environment) {
        securityService.checkGlobalFunction(EnvironmentDelete::class.java)
        environmentRepository.delete(env)
    }
}