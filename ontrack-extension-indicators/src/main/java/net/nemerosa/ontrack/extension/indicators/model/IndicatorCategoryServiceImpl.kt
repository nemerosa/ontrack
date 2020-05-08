package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryNotFoundException
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import org.springframework.stereotype.Service

@Service
class IndicatorCategoryServiceImpl : IndicatorCategoryService {

    companion object {
        const val SERVICES = 1
        const val DELIVERY = 2
        const val HELM = 3
    }

    private val categories = listOf(
            IndicatorCategory(id = SERVICES, name = "Services principles"),
            IndicatorCategory(id = DELIVERY, name = "Delivery principles"),
            IndicatorCategory(id = HELM, name = "Helm principles")
    ).associateBy { it.id }

    override fun findCategoryById(id: Int): IndicatorCategory? = categories[id]

    override fun getCategory(id: Int): IndicatorCategory {
        return findCategoryById(id) ?: throw IndicatorCategoryNotFoundException(id)
    }

}