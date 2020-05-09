package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.extension.indicators.acl.IndicatorPortfolioManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class IndicatorPortfolioServiceImpl(
        private val securityService: SecurityService,
        private val storageService: StorageService
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

    companion object {
        private val STORE = IndicatorPortfolio::class.java.name
    }

}