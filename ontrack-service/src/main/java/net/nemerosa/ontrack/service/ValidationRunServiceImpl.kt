package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunData
import net.nemerosa.ontrack.model.structure.ValidationRunService
import net.nemerosa.ontrack.repository.ValidationRunRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ValidationRunServiceImpl(
    private val securityService: SecurityService,
    private val validationRunRepository: ValidationRunRepository,
) : ValidationRunService {

    override fun updateValidationRunData(run: ValidationRun, data: ValidationRunData<*>?): ValidationRun {
        securityService.checkProjectFunction(run, ProjectEdit::class.java)
        return validationRunRepository.updateValidationRunData(run, data)
    }

}