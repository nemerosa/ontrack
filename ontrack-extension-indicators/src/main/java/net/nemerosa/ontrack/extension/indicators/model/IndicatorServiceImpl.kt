package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.store.IndicatorStore
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.Signature
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IndicatorServiceImpl(
        private val indicatorStore: IndicatorStore,
        private val indicatorTypeService: IndicatorTypeService
) : IndicatorService {

    override fun getProjectIndicators(project: Project, all: Boolean): List<Indicator<*>> {
        // Gets all the types
        val types = indicatorTypeService.findAll()
        // Gets the values
        return types.map {
            loadIndicator(project, it)
        }.filter {
            all || it.value != null
        }
    }

    override fun getProjectIndicator(project: Project, typeId: Int): Indicator<*> {
        val type = indicatorTypeService.getTypeById(typeId)
        return loadIndicator(project, type)
    }

    private fun <T, C> loadIndicator(project: Project, type: IndicatorType<T, C>): Indicator<T> {
        val stored = indicatorStore.loadIndicator(project, type.id)
        return if (stored != null) {
            val value = type.fromStoredJson(stored.value)
            Indicator(
                    type = type,
                    value = value,
                    status = stored.status,
                    comment = stored.comment,
                    signature = stored.signature
            )
        } else {
            Indicator(
                    type = type,
                    value = null,
                    status = null,
                    comment = null,
                    signature = Signature.anonymous()
            )
        }
    }
}