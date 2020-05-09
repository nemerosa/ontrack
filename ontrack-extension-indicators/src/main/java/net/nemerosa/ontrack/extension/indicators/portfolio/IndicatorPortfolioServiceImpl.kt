package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioManagement
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class IndicatorPortfolioServiceImpl(
        private val securityService: SecurityService,
        private val storageService: StorageService,
        private val labelManagementService: LabelManagementService
) : IndicatorPortfolioService {

    override fun createPortfolio(name: String): IndicatorPortfolio {
        securityService.checkGlobalFunction(IndicatorPortfolioManagement::class.java)
        val id = UUID.randomUUID().toString()
        val portfolio = IndicatorPortfolio(
                id,
                name,
                emptyList(),
                emptyList()
        )
        storageService.store(STORE, id, portfolio)
        return portfolio
    }

    override fun getPortfolioLabels(portfolio: IndicatorPortfolio): List<Label> {
        return portfolio.labels.mapNotNull {
            labelManagementService.findLabelById(it)
        }
    }

    override fun findAll(): List<IndicatorPortfolio> {
        return storageService.getKeys(STORE).mapNotNull { key ->
            storageService.retrieve(STORE, key, IndicatorPortfolio::class.java).orElse(null)
        }
    }

    companion object {
        private val STORE = IndicatorPortfolio::class.java.name
    }

}