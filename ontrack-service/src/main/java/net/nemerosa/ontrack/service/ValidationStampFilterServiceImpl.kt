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
        checkUpdateAuthorisations(filter)
        return repository.newValidationStampFilter(filter)
    }

    override fun saveValidationStampFilter(filter: ValidationStampFilter) {
        checkUpdateAuthorisations(filter)
        repository.saveValidationStampFilter(filter)
    }

    override fun deleteValidationStampFilter(filter: ValidationStampFilter): Ack {
        checkUpdateAuthorisations(filter)
        return repository.deleteValidationStampFilter(filter.id)
    }

    private fun checkUpdateAuthorisations(filter: ValidationStampFilter) {
        filter.project?.let {
            securityService.checkProjectFunction(it, ValidationStampFilterMgt::class.java)
        } ?: filter.branch?.let {
            securityService.checkProjectFunction(it, ValidationStampFilterCreate::class.java)
        } ?: securityService.checkGlobalFunction(GlobalSettings::class.java)
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
