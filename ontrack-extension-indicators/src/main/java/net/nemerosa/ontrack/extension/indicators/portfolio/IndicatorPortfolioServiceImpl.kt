package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioManagement
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryListener
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IndicatorPortfolioServiceImpl(
        private val structureService: StructureService,
        private val securityService: SecurityService,
        private val storageService: StorageService,
        private val labelManagementService: LabelManagementService,
        private val projectLabelManagementService: ProjectLabelManagementService,
        private val indicatorCategoryService: IndicatorCategoryService
) : IndicatorPortfolioService, IndicatorCategoryListener {

    init {
        indicatorCategoryService.registerCategoryListener(this)
    }

    override fun onCategoryDeleted(category: IndicatorCategory) {
        findAll().forEach { portfolio ->
            if (category.id in portfolio.categories) {
                val newCategories = portfolio.categories - category.id
                updatePortfolio(
                        portfolio.id,
                        PortfolioUpdateForm(
                                categories = newCategories
                        )
                )
            }
        }
    }

    override fun createPortfolio(id: String, name: String): IndicatorPortfolio {
        securityService.checkGlobalFunction(IndicatorPortfolioManagement::class.java)
        val existing = findPortfolioById(id)
        if (existing != null) {
            throw IndicatorPortfolioIdAlreadyExistingException(id)
        } else {
            val portfolio = IndicatorPortfolio(
                    id = id,
                    name = name,
                    label = null,
                    categories = emptyList()
            )
            storageService.store(STORE, id, portfolio)
            return portfolio
        }
    }

    override fun updatePortfolio(id: String, input: PortfolioUpdateForm): IndicatorPortfolio {
        securityService.checkGlobalFunction(IndicatorPortfolioManagement::class.java)
        val existing = findPortfolioById(id) ?: throw IndicatorPortfolioNotFoundException(id)
        val name = if (!input.name.isNullOrBlank()) {
            input.name
        } else {
            existing.name
        }
        val label = input.label ?: existing.label
        val categories = input.categories ?: existing.categories
        val newRecord = IndicatorPortfolio(
                id = id,
                name = name,
                label = label,
                categories = categories
        )
        storageService.store(STORE, id, newRecord)
        return newRecord
    }

    override fun deletePortfolio(id: String) {
        securityService.checkGlobalFunction(IndicatorPortfolioManagement::class.java)
        storageService.delete(STORE, id)
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

    override fun findPortfolioById(id: String): IndicatorPortfolio? {
        return storageService.retrieve(STORE, id, IndicatorPortfolio::class.java).orElse(null)
    }

    override fun findAll(): List<IndicatorPortfolio> {
        return storageService.getKeys(STORE).mapNotNull { key ->
            storageService.retrieve(STORE, key, IndicatorPortfolio::class.java).orElse(null)
        }.sortedBy { it.name }
    }

    override fun getPortfolioOfPortfolios(): IndicatorPortfolioOfPortfolios {
        // Checks we have access to the projects
        structureService.projectList
        return storageService.retrieve(STORE_PORTFOLIO_OF_PORTFOLIOS, PORTFOLIO_OF_PORTFOLIOS, IndicatorPortfolioOfPortfolios::class.java)
                .orElse(IndicatorPortfolioOfPortfolios(
                        categories = emptyList()
                ))
    }

    override fun savePortfolioOfPortfolios(input: PortfolioGlobalIndicators): IndicatorPortfolioOfPortfolios {
        val portfolio = IndicatorPortfolioOfPortfolios(
                input.categories.filter {
                    indicatorCategoryService.findCategoryById(it) != null
                }
        )
        storageService.store(STORE_PORTFOLIO_OF_PORTFOLIOS, PORTFOLIO_OF_PORTFOLIOS, portfolio)
        return portfolio
    }

    override fun findPortfolioByProject(project: Project): List<IndicatorPortfolio> {
        // Gets the labels for this project
        val labels = projectLabelManagementService.getLabelsForProject(project).map { it.id }.toSet()
        // Gets all portfolios and filter on label
        return if (labels.isEmpty()) {
            emptyList()
        } else {
            findAll().filter { portfolio ->
                portfolio.label != null && portfolio.label in labels
            }
        }
    }

    companion object {
        private val STORE = IndicatorPortfolio::class.java.name
        private val STORE_PORTFOLIO_OF_PORTFOLIOS = IndicatorPortfolioOfPortfolios::class.java.name
        private const val PORTFOLIO_OF_PORTFOLIOS = "0"
    }

}