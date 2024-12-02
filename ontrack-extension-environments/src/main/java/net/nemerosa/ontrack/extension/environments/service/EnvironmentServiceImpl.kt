package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.EnvironmentFilter
import net.nemerosa.ontrack.extension.environments.EnvironmentsLicense
import net.nemerosa.ontrack.extension.environments.events.EnvironmentsEventsFactory
import net.nemerosa.ontrack.extension.environments.security.EnvironmentDelete
import net.nemerosa.ontrack.extension.environments.security.EnvironmentList
import net.nemerosa.ontrack.extension.environments.security.EnvironmentSave
import net.nemerosa.ontrack.extension.environments.storage.EnvironmentNameAlreadyExists
import net.nemerosa.ontrack.extension.environments.storage.EnvironmentRepository
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EnvironmentServiceImpl(
    private val environmentRepository: EnvironmentRepository,
    private val securityService: SecurityService,
    private val eventPostService: EventPostService,
    private val environmentsEventsFactory: EnvironmentsEventsFactory,
    private val environmentsLicense: EnvironmentsLicense,
) : EnvironmentService {

    override fun save(environment: Environment) {
        securityService.checkGlobalFunction(EnvironmentSave::class.java)
        val existing = environmentRepository.findByName(environment.name)
        if (existing != null && existing.id != environment.id) {
            throw EnvironmentNameAlreadyExists(environment.name)
        }
        if (existing == null) {
            val maxEnvironments = environmentsLicense.maxEnvironments
            if (maxEnvironments > 0) {
                val countEnvironments = environmentRepository.countEnvironments
                if (countEnvironments >= maxEnvironments) {
                    environmentsLicense.maxEnvironmentsReached()
                }
            }
        }
        environmentRepository.save(environment)
        if (existing != null) {
            eventPostService.post(environmentsEventsFactory.environmentUpdated(environment))
        } else {
            eventPostService.post(environmentsEventsFactory.environmentCreation(environment))
        }
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
        eventPostService.post(environmentsEventsFactory.environmentDeleted(env))
        environmentRepository.delete(env)
    }
}