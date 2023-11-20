package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.ValidationStampFilterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class ValidationStampFilterServiceImpl(
        private val repository: ValidationStampFilterRepository,
        private val securityService: SecurityService
) : ValidationStampFilterService {

    override fun getGlobalValidationStampFilters(): List<ValidationStampFilter> {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return repository.globalValidationStampFilters
    }

    override fun getProjectValidationStampFilters(project: Project, includeAll: Boolean): List<ValidationStampFilter> {
        securityService.checkProjectFunction(project, ProjectView::class.java)
        // Index by names
        val filters = TreeMap<String, ValidationStampFilter>()
        // Gets the global filters
        if (includeAll) {
            repository.globalValidationStampFilters.forEach { f -> filters[f.name] = f }
        }
        // Gets for project
        repository.getProjectValidationStampFilters(project).forEach { f -> filters[f.name] = f }
        // OK
        return filters.values.toList()
    }

    override fun getBranchValidationStampFilters(branch: Branch, includeAll: Boolean): List<ValidationStampFilter> {
        securityService.checkProjectFunction(branch, ProjectView::class.java)
        // Index by names
        val filters = TreeMap<String, ValidationStampFilter>()
        // Gets the project filters
        if (includeAll) {
            repository.globalValidationStampFilters.forEach { f -> filters[f.name] = f }
            repository.getProjectValidationStampFilters(branch.project).forEach { f -> filters[f.name] = f }
        }
        // Gets for branch
        repository.getBranchValidationStampFilters(branch).forEach { f -> filters[f.name] = f }
        // OK
        return filters.values.toList()
    }

    override fun getValidationStampFilterByName(branch: Branch, name: String): Optional<ValidationStampFilter> {
        securityService.checkProjectFunction(branch, ProjectView::class.java)
        return repository.getValidationStampFilterByName(branch, name)
    }

    override fun newValidationStampFilter(filter: ValidationStampFilter): ValidationStampFilter {
        securityService.checkUpdateAuthorisations(filter)
        return repository.newValidationStampFilter(filter)
    }

    override fun saveValidationStampFilter(filter: ValidationStampFilter) {
        securityService.checkUpdateAuthorisations(filter)
        repository.saveValidationStampFilter(filter)
    }

    override fun deleteValidationStampFilter(filter: ValidationStampFilter): Ack {
        securityService.checkUpdateAuthorisations(filter)
        return repository.deleteValidationStampFilter(filter.id)
    }

    override fun shareValidationStampFilter(filter: ValidationStampFilter, project: Project): ValidationStampFilter {
        securityService.checkProjectFunction(project, ValidationStampFilterShare::class.java)
        return repository.shareValidationStampFilter(filter, project)
    }

    override fun shareValidationStampFilter(filter: ValidationStampFilter): ValidationStampFilter {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return repository.shareValidationStampFilter(filter)
    }

    override fun getValidationStampFilter(id: ID): ValidationStampFilter {
        return repository.getValidationStampFilter(id)
    }
}
