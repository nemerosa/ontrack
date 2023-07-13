package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isProjectFunctionGranted
import net.nemerosa.ontrack.model.structure.ValidationStampService
import net.nemerosa.ontrack.repository.ValidationStampRepository
import org.springframework.stereotype.Service

@Service
class ValidationStampServiceImpl(
    private val securityService: SecurityService,
    private val validationStampRepository: ValidationStampRepository,
) : ValidationStampService {

    override fun findValidationStampNames(token: String): List<String> =
        validationStampRepository.findByToken(token)
            .filter { securityService.isProjectFunctionGranted<ProjectView>(it) }
            .groupBy { it.name }
            .keys
            .sorted()

}