package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorViewManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IndicatorViewServiceImpl(
    private val storageService: StorageService,
    private val securityService: SecurityService
) : IndicatorViewService {

    override fun getIndicatorViews(): List<IndicatorView> =
        storageService.getData(STORE_INDICATOR_VIEWS, IndicatorView::class.java).values.sortedBy { it.name }

    override fun saveIndicatorView(view: IndicatorView) {
        securityService.checkGlobalFunction(IndicatorViewManagement::class.java)
        storageService.store(
            STORE_INDICATOR_VIEWS,
            view.name,
            view
        )
    }

    override fun findIndicatorViewByName(name: String): IndicatorView? =
        storageService.retrieve(STORE_INDICATOR_VIEWS, name, IndicatorView::class.java).getOrNull()

    override fun deleteIndicatorView(name: String) {
        securityService.checkGlobalFunction(IndicatorViewManagement::class.java)
        storageService.store(STORE_INDICATOR_VIEWS, name, null)
    }

    companion object {
        private val STORE_INDICATOR_VIEWS: String = IndicatorView::class.java.name
    }
}