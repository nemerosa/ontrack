package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioManagement
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class IndicatorPortfolioServiceImpl(
        private val structureService: StructureService,
        private val securityService: SecurityService,
        private val storageService: StorageService,
        private val labelManagementService: LabelManagementService,
        private val projectLabelManagementService: ProjectLabelManagementService
) : IndicatorPortfolioService {

    override fun createPortfolio(name: String): IndicatorPortfolio {
        securityService.checkGlobalFunction(IndicatorPortfolioManagement::class.java)
        val id = UUID.randomUUID().toString()
        val portfolio = IndicatorPortfolio(
                id = id,
                name = name,
                label = null,
                types = emptyList()
        )
        storageService.store(STORE, id, portfolio)
        return portfolio
    }

    override fun getPortfolioLabel(portfolio: IndicatorPortfolio): Label? =
            portfolio.label?.let {
                labelManagementService.findLabelById(it)
            }

    override fun getPortfolioProjects(portfolio: IndicatorPortfolio): List<Project> =
            getPortfolioLabel(portfolio)?.let { label ->
                projectLabelManagementService.getProjectsForLabel(label)
            }?.map {
                structureService.getProject(it)
            } ?: emptyList()

    override fun findAll(): List<IndicatorPortfolio> {
        return storageService.getKeys(STORE).mapNotNull { key ->
            storageService.retrieve(STORE, key, IndicatorPortfolio::class.java).orElse(null)
        }.sortedBy { it.name }
    }

    companion object {
        private val STORE = IndicatorPortfolio::class.java.name
    }

}