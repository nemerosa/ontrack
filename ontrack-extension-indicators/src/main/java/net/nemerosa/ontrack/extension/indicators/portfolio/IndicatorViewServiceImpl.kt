package net.nemerosa.ontrack.extension.indicators.portfolio

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorViewManagement
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class IndicatorViewServiceImpl(
    private val storageService: StorageService,
    private val securityService: SecurityService
) : IndicatorViewService {

    override fun getIndicatorViews(): List<IndicatorView> =
        storageService.getData(STORE_INDICATOR_VIEWS, IndicatorView::class.java).values.sortedBy { it.name }

    override fun saveIndicatorView(view: IndicatorView): IndicatorView {
        securityService.checkGlobalFunction(IndicatorViewManagement::class.java)

        return if (view.id.isNotBlank()) {
            // Gets the existing record
            val existing = findIndicatorViewById(view.id)
            if (existing != null) {
                // Update
                updateIndicatorView(view)
            } else {
                throw IndicatorViewIDNotFoundException(view.id)
            }
        } else {
            // Creation
            createIndicatorView(view)
        }
    }

    private fun createIndicatorView(view: IndicatorView): IndicatorView {
        val existing = findIndicatorViewByName(view.name)
        if (existing != null) {
            throw IndicatorViewNameAlreadyExistsException(view.name)
        }
        val uuid = UUID.randomUUID().toString()
        val record = IndicatorView(
            id = uuid,
            name = view.name,
            categories = view.categories
        )
        storageService.store(STORE_INDICATOR_VIEWS, uuid, record)
        return record
    }

    private fun updateIndicatorView(view: IndicatorView): IndicatorView {
        val existing = findIndicatorViewByName(view.name)
        if (existing != null && existing.id != view.id) {
            throw IndicatorViewNameAlreadyExistsException(view.name)
        }
        storageService.store(STORE_INDICATOR_VIEWS, view.id, view)
        return view
    }

    override fun findIndicatorViewById(id: String): IndicatorView? =
        storageService.retrieve(STORE_INDICATOR_VIEWS, id, IndicatorView::class.java).getOrNull()

    override fun findIndicatorViewByName(name: String): IndicatorView? =
        getIndicatorViews().findLast { it.name == name }

    override fun deleteIndicatorView(id: String): Ack {
        securityService.checkGlobalFunction(IndicatorViewManagement::class.java)
        storageService.store(STORE_INDICATOR_VIEWS, id, null)
        return Ack.OK
    }

    companion object {
        private val STORE_INDICATOR_VIEWS: String = IndicatorView::class.java.name
    }
}