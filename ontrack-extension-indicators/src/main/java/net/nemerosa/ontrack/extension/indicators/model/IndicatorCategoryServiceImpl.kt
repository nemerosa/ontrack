package net.nemerosa.ontrack.extension.indicators.model

import org.springframework.stereotype.Service

@Service
class IndicatorCategoryServiceImpl : IndicatorCategoryService {

    companion object {
        const val SERVICES = "f3ac284c-6da6-477b-8887-1569b59e397b"
        const val DELIVERY = "c6dfaf82-0739-46a2-85b8-0964d596b5d8"
        const val HELM = "da5bdef6-b7e9-4c30-a1fe-ee671bec466c"
    }

    private val categories = listOf(
            IndicatorCategory(id = SERVICES, name = "Services principles"),
            IndicatorCategory(id = DELIVERY, name = "Delivery principles"),
            IndicatorCategory(id = HELM, name = "Helm principles")
    ).associateBy { it.id }

    override fun findCategoryById(id: String): IndicatorCategory? = categories[id]

    override fun getCategory(id: String): IndicatorCategory {
        return findCategoryById(id) ?: throw IndicatorCategoryNotFoundException(id)
    }

    override fun findAll(): List<IndicatorCategory> {
        return categories.values.sortedBy { it.name }
    }

}